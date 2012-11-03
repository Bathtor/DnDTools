package com.larskroll.dndtools

import java.io.File
import org.parboiled.common.FileUtils
import akka.pattern.{ ask, pipe }
import akka.util.duration._
import akka.util.Timeout
import akka.actor.{ ActorLogging, Props, Actor }
import spray.routing.{ HttpService, RequestContext }
import spray.routing.directives._
import spray.can.server.HttpServer
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import StatusCodes._
import MediaTypes._
import CachingDirectives._
import spray.routing.Route
import scala.xml.Elem
import akka.dispatch.Await
import com.larskroll.dndtools.utils.Spell
import scala.util.Random
import com.larskroll.dndtools.utils.Spells
import com.larskroll.dndtools.utils.SpellList
import java.util.Date

class ServiceRouterActor extends Actor with ServiceRouter {
	// the HttpService trait defines only one abstract member, which
	// connects the services environment to the enclosing actor or test
	def actorRefFactory = context

	def receive = runRoute(primaryRoute);
}

trait ServiceRouter extends HttpService {

	val rand = new Random();

	val spellService = actorRefFactory.actorOf(Props[SpellService]);

	implicit val timeout = Timeout(5.seconds);

	val respondWithHTML = respondWithMediaType(`text/html`) & detachTo(singleRequestServiceActor);

	val primaryRoute: Route = {
		get {
			path("favicon.ico") {
				getFromResource("WEB-INF/favicon.ico")
			} ~ path("static" / PathElement) { file =>
				getFromResource("WEB-INF/" + file)
			} ~ path("spells" / HexLongNumber) { id =>
				respondWithHTML { ctx =>
					ctx.complete {
						sendSpellList(id)
					}
				}
			} ~ path("spells" / HexLongNumber / "words") { id =>
				respondWithHTML { ctx =>
					ctx.complete {
						sendWords(id)
					}
				}
			} ~ path("spells") {ctx =>
				var key = -1l;
				while (key < 0) {
					key = rand.nextLong;
				}
				ctx.redirect("spells/" + key.toHexString, SeeOther);
			} ~ path("stop") { ctx =>
				ctx.complete("Shutting down in 1 second...")
				actorSystem.scheduler.scheduleOnce(1.seconds)(systemShutdown);
			}
		} ~ post {
			path("spells" / HexLongNumber / "spell" / LongNumber) { (id, spellId) =>
				respondWithHTML { ctx =>
					ctx.complete {
						toggleSpell(id, spellId)
					}
				}
			}
		} ~ { ctx =>
			println("Could not handle request " + ctx.request);
			ctx.complete("Could not handle request " + ctx.request)
		}

	}

	//	def deferToSpellService(ctx: RequestContext): Unit = {
	//		println("deferring...");
	//		spellService ! ctx;
	//	}

	def systemShutdown(): Unit = {
		println("System shutting down...");
		actorSystem.shutdown();
	}

	def toggleSpell(id: Long, spellId: Long): Elem = {
		val f = spellService ? ToggleSpell(spellId, id);
		val fS = spellService ? GetSpell(spellId);
		val fR = for {
			a <- f.mapTo[ToggleReply]
			b <- fS.mapTo[Option[Spell]]
		} yield (a, b)
		val res = Await.result(fR, 5 seconds);
		res match {
			case (EntryInserted, s) => spellItem(spellId, s.getOrElse(null), true);
			case (EntryDeleted, s) => spellItem(spellId, s.getOrElse(null), false);
		}
	}

	def sendWords(key: Long): Elem = {
		val fW = spellService ? GetWords(key);
		val words = Await.result(fW, 5 seconds).asInstanceOf[Words];
		return wordBlock(words.num);
	}

	def sendSpellList(id: Long): Elem = {
		println("Getting spelllist for " + id);
		val fS = spellService ? GetSpells;
		val fM = spellService ? GetMemorised(id);
		val fR = for {
			a <- fS.mapTo[SpellList]
			b <- fM.mapTo[List[Long]]
		} yield (a, b)
		val (spells, memorised) = Await.result(fR, 5 seconds);
		val listitems = scala.collection.mutable.ListBuffer.empty[Elem];
		for (i <- 0 to 9) {
			val words = Spells.words(i);
			val header = <h2 id={ "sl-" + i.toString }>
				Level&nbsp;{ i.toString }
				<font class="smalltext">
					{ words.toString }
					Word(s) per Spell
				</font>
			</h2>
			listitems += header;
			spells.byLevel(i).orderByName.spells foreach {
				case s: Spell => {
					val liitem = spellItem(s.id, s, memorised.contains(s.id));
					listitems += liitem;
				}
			}
		}
		val htmllist = <div id="spells">{
			listitems.readOnly
		}</div>
		return bodyWrap(id, htmllist);
	}

	def spellItem(id: Long, s: Spell, mem: Boolean): Elem = {
		if (s != null) {
			val inMem = if (mem) "inMemorySpell" else "";
			val checkbox = if (mem) {
				<input type="checkbox" name={ "check-" + s.id.toString } value={ s.id.toString } checked="checked"/>
			} else {
				<input type="checkbox" name={ "check-" + s.id.toString } value={ s.id.toString }/>
			}
			val liitem = <p class={ inMem } title={ s.id.toString } id={ "spell-" + s.id.toString }>
				{ checkbox }
				{ s.name }
				({ s.school + " " + s.level.toString }
				)
				<font class="smalltext">
					&nbsp;&mdash;&nbsp;{ s.desc }
					&nbsp;&rarr;&nbsp;{ s.ref }
				</font>
			</p>;
			return liitem;
		} else {
			val text = "No such Spell: " + id;
			return <script language="javascript">alert('{ text }')</script>
		}
	}

	def bodyWrap(id: Long, body: Elem): Elem = <html>
		<head>
			<meta charset="utf-8"/>
			<link rel="stylesheet" type="text/css" href="/static/dnd.css"/>
			<script src="https://www.google.com/jsapi"></script>
			<script>
				google.load('jquery', '1.3.1');
			</script>
			<script language="javascript" src="/static/scripts.js"></script>
		</head>
		<body>
			<div id="header">
				<h1>D&amp;D Spell List</h1>
			</div>
			<div id="navigation">
				<ul>
					<li><a href="">ID: { id.toHexString }</a></li>
					<li id="words"><a href="">Loading...</a></li>
				</ul>
			</div>
			<div id="content">
				{ body }
			</div>
			<div id="footer">
				{ footerList }
			</div>
		</body>
	</html>

	def wordBlock(words: Int): Elem = {
		<li id="words"><a href="">Currently Memorised Words: { words }</a></li>
	}

	def footerList(): Elem = {
		val listitems = scala.collection.mutable.ListBuffer.empty[Elem];
		for (i <- 9 to 0 by -1) {
			val listitem = <li><a href={ "#sl-" + i.toString }>Level { i.toString }</a></li>
			listitems += listitem;
		}
		return <ul>{ listitems.readOnly }</ul>
	}
}