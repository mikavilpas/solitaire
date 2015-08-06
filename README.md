# solitaire

FIXME: Write a one-line description of your library/project.

## Overview

FIXME: Write a paragraph about the library/project and highlight its goals.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To get connect cider (in emacs) to this repl, do `cider-connect` to
port 7888. This connects to figwheel's environment, a clojure repl. To
connect to the cljs repl, see core.cljs. It does this, namely:

```lisp
(do
  (use 'figwheel-sidecar.repl-api)
  (cljs-repl))
```

You have to eval those in the repl buffer, not inline. It doesn't work
for some reason.

---

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
