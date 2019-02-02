![Gilded Gauge](http://gildedgauge.club/gildedgauge.gif)

# [Gilded Gauge](http://gildedgauge.club)

#### by [Dan Motzenbecker](http://oxism.com)

_Gilded Gauge_ is an experiment in visualizing relative wealth in terms viewers
may find more natural to grasp.

Enormous numbers become tangible via comparisons to the Fall of Rome, the distant
future, and cascades of emoji commodities.

Each falling menagerie represents an exact representation of the value in
question, down to the exact dollar.

### Development

Open a dev server on `3449`:

```
$ ./scripts/start
```

Watch and recompile style source:

```
$ ./scripts/style-watch.sh
```

Update ranking data:

```
$ ./scripts/get-rankings.sh
```

### Production

Build JS and CSS bundles:

```
$ ./scripts/build
```

Or run the [.distilla](https://github.com/dmotz/distilla) config.
