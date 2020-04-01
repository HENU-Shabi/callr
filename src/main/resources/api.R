#' Echo the parameter that was sent in
#' @png
#' @post /plot/<strAvg>
function(req, strAvg)
  {
  raw <- req$postBody;
  res <- read.csv(text = raw);
  r <- nrow(res);
  gmax <- res[order(-res[, 1]),];
  gen <- res[order(-res[, 3]),];
  g <- nrow(gen);
  r1 <- round(r / 2);
  if (gen[r1, 3] == gen[r1 + 1, 3]) { suba <- gen[r1, 3] - gen[r1 - 1, 3]; subb <- gen[r1 + 1, 3] - gen[r1 + 2, 3]; if (abs(suba) > abs(subb)) { gen.up <- gen[1:r1,]; gen.low <- gen[(r1 + 1):r,] }else { gen.up <- gen[1:(r1 + 1),]; gen.low <- gen[(r1 + 2):r,] } }else { gen.up <- gen[1:r1,]; gen.low <- gen[(r1 + 1):r,] };
  gen.up[, 3] <- 1;
  gen.low[, 3] <- 0;
  gen <- rbind(gen.up, gen.low);
  #gen.surv<-survfit(Surv(gen[,1],gen[,2])~gen[,3]);
  gen.surv <- do.call(survfit, args = list(formula = Surv(gen[, 1], gen[, 2]) ~ gen[, 3]));
  gen.dif <- survdiff(Surv(gen[, 1], gen[, 2]) ~ gen[, 3]);
  p.cox <- summary(coxph(Surv(gen[, 1], gen[, 2]) ~ gen[, 3]));
  p.val <- p.cox$coefficients[5];
  p.hr <- p.cox$coefficients[2];
  p.l <- p.cox$conf.int[3];
  p.t <- p.cox$conf.int[4];
  ggsurv <- ggsurvplot(fit = gen.surv, data = gen, risk.table = TRUE, xlab = "Months", palette = c("red", "green"), main = "Survival curve", font.main = c(16, "bold", "darkblue"), font.x = c(16, "bold", "black"), font.y = c(16, "bold", "black"), font.tickslab = c(16, "plain", "black"), legend.title = strAvg, legend.labs = c("Upper 50%", "Other 50%"), legend = "none");
  ggsurv$table <- ggsurv$table + theme(axis.text.y = element_text(colour = 'black'));
  ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.80, y = 0.97, label = "Upper 50%", size = 4, color = 'green');
  ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.80, y = 0.92, label = "Other 50%", size = 4, color = 'red');
  if (p.val < 0.0001) { ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.12, y = 0.15, label = "p<0.0001", size = 5) } else { ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.12, y = 0.15, label = paste("p=", round(p.val, 4)), size = 5) };
  ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.12 + 2, y = 0.09, label = paste("HR=", round(p.hr, 4)), size = 5);
  ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.26, y = 0.03, label = paste('( 95%CI,', round(p.l, 4), '-', round(p.t, 4), ')'), size = 5);
  ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.85, y = 0.05, label = "osppc", size = 5, fontface = 'italic');
  ggsurv$plot <- ggsurv$plot + ggplot2::annotate("text", x = gmax[1, 1] * 0.5, y = 0.95, label = strAvg, size = 5);
  print(ggsurv);
  #ggsave(file = "plot.png", width=6,height=6, type = "cairo",print(ggsurv))
}