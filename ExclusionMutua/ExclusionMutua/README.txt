The main class is org.sistdist.exclusionmutua.FileProcess;

It creates 3 file process
Once it starts running it accepts some inputs/commands:
    q or quit -> stops the program
    process_number|OPEN
    process_number|READ
    process_number|CLOSE
        where process_number is one from 1,2,3
    process_number|WRITE|line_to_append
        where line_to_append is a string that will be appended to the local file
    process_number|UPDATE|new_file_content
        where new_file_content will be the content of the local file

A file should be opened first before doing all others operations        
examples:
  1|open
  1|read
  1|write|hola
  1|close
  q