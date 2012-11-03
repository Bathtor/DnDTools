#! /usr/bin/env python
""" Cleaning script based on .gitignore file, the 'find' and 'rm' command.
It removes files with .gitignore pattern. 

This script reads the .gitignore file, parses the patterns found there to a
shell remove command and then executes it.

- With the 'dirs' option it also removes directories.
- With the 'debug' option it just prints the command it would execute and exits.

Uses the 'find' and 'rm' Linux / *NIX / OS X commands, which restricts it to 
these platforms.

Note: be careful with this script, it's quite powerful.
"""

import os, sys

def change_to_file_dir():
        os.chdir(os.path.abspath(os.path.dirname(__file__)))

def read_clean_patterns():
        try:
                with open('.gitignore', 'r') as f:
                        clean_patterns = f.readlines()
        except IOError as err:
                sys.stderr.write('Could not open .gitignore file: ', err)
                sys.exit(2)
        return clean_patterns

def create_clean_command(clean_patterns):
        clean_patterns = ['-o -name "' + item.replace('\n', '') + '" '
                for item in clean_patterns]
        first = 0
        clean_patterns[first] = clean_patterns[first].replace('-o ', '')
        clean_command = str()
        for x in clean_patterns:
                clean_command += x
        if len(sys.argv) > 1 and sys.argv[1] == 'dirs':
                clean_command = ('find . \( ' + 
                                 clean_command + '\) -exec rm -vrf "{}" \;')
        else:
                clean_command = ('find . \( ' + 
                                 clean_command + '\) -exec rm -vf "{}" \;')
        return clean_command

if __name__ == '__main__':
        if (len(sys.argv) > 1 and 
            sys.argv[1] != 'dirs' and 
            sys.argv[1] != 'debug'):
                print('Usage: clean [dirs] [debug]')
                print('   dirs: recursive, remove directories too')
                print('   debug: only print command, don\'t execute')
                sys.exit()

        change_to_file_dir()
        clean_patterns = read_clean_patterns()
        clean_command = create_clean_command(clean_patterns)
        if sys.argv[-1] == 'debug':
                print ('Clean command is:', clean_command, sep='\n')
        else:
                os.system(clean_command)
