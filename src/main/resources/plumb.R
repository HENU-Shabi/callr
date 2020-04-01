args <- commandArgs(trailingOnly = TRUE)
r <- plumb(args[2])
r$run(port = as.integer(args[1]))