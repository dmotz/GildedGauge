![Gilded Gauge](http://gildedgauge.club/gildedgauge.gif)
# [Gilded Gauge](http://gildedgauge.club)
#### by [Dan Motzenbecker](http://oxism.com)

*Gilded Gauge* is an experiment in visualizing relative wealth in terms viewers
may find more natural to grasp.

Enormous numbers become tangible via comparisons to the Fall of Rome, the distant
future, and cascades of emoji commodities.

Each falling menagerie represents an exact representation of the value in
question, down to the exact dollar.


### Development

Open a dev server on `3449`:

```
$ lein figwheel
```

Watch style source:

```
$ ./style-build.sh
```

### Production

Build JS bundle:

```
$ lein cljsbuild once min
```

Build CSS bundle:

```
$ stylus -u nib -c resources/public/css/style.styl
```

Or run the [.distilla](https://github.com/dmotz/distilla) config.
