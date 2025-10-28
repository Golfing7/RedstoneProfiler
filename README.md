# RedstoneProfiler
A plugin built to empirically evaluate the performance of the redstone engines available in [PaperMC](https://github.com/PaperMC).

Do note that this profiler is NOT built to profile anything more than redstone wire.
In no way should this profiler be used to evaluate circuits with any other redstone component.

## Metrics
This plugin is capable of generating two types of metrics.
1) Runtime - measured in nanos
2) Physics Updates

## Experiments
Several types of experiments are provided in the form of schematics.
These schematics are pasted via the [WorldEdit API](https://worldedit.enginehub.org/en/latest/api/).
The experiments use emerald blocks to denote where redstone blocks should be placed when powering on.

## Building
```shell
./gradlew jar
```