function addClickHandlers() {
	$("#spells p").live("click", toggleSpell);
}

function toggleSpell() {
	var id = $(this).attr('id').split('-')[1];
	var el = $(this);
	el.html('Updating...');
	$.post(baseUrl()+'/spell/'+id, function(data) {
		el.replaceWith(data);
		updateWordList();
	});
}

function baseUrl() {
	var loc = $(location).attr('href');
	var noParams = loc.split('?')[0];
	var noIds = noParams.split('#')[0];
	return noIds;
}

function updateWordList() {
	var el = $("#words");
	el.html("Loading...");
	$.get(baseUrl()+'/words', function(data) {
		el.replaceWith(data);
	});
}

$(function() {
	addClickHandlers();
	updateWordList();
});