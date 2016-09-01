library(ggplot2)
library(reshape)
foo = read.table("heatmap_further-work.csv", sep="\t", header=TRUE)
foomelt = melt(foo)
ggplot(foomelt, aes(x=Vars, y=variable, fill=value)) + geom_tile() + scale_fill_distiller(palette="Spectral")
ggsave('heatmap.pdf', width=15, height=6, dpi=300)

