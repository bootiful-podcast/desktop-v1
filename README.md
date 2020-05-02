# Desktop Client

![Desktop Client](https://github.com/bootiful-podcast/desktop/workflows/Desktop%20Client/badge.svg)


An experimental take on a desktop UI to the podcast publication usecase. 

In order to run this you'll need to run [the API server](https://github.com/bootiful-podcast/api). 

In order to run this you'll need to run [the Python-based Processor](https://github.com/bootiful-podcast/processor).

In order to run this you'll want to run [the Site Generator, which then updates a local copy of the website](https://github.com/bootiful-podcast/site-generator).
 
You can run this with a `spring.profiles.active=dev` to activate the `dev` profile and preload a `Podcast` instance to load. It depends on files for a particular real, past, podcast residing in your `$HOME/Dropbox/` folder.  

## To Do
* Show a publication progress bar (animated gif?) while it's being processed by the service.
* Choose a JavaFX window icon 
* Figure out how to make this a MacOS application
* Setup CI/CD
* Some sort of UI to open older podcasts for editing?

## Attributions
* This program uses [this icon](http://chittagongit.com/icon/icon-connected-8.html) to indicate connectivity.
* This program uses [this icon](https://loading.io/spinner/blocks/-rotating-squares-preloader-gif) to indicate progress.

    
