# [Gilded Gauge](https://oxism.com/GildedGauge/)

#### by [Dan Motzenbecker](http://oxism.com)

https://github.com/dmotz/GildedGauge/assets/302080/7b86bf70-3997-4323-8763-0eff83f6ecc8

_Gilded Gauge_ is an experiment in visualizing relative wealth in terms viewers
may find more natural to grasp.

Enormous numbers become tangible via comparisons to the Fall of Rome, the distant
future, and cascades of emoji commodities.

Each falling menagerie represents a concrete representation of the value in question,
down to the precise dollar.

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
