package ehealth.group1.backend.helper.graphics;

import ehealth.group1.backend.entity.GraphicsSettings;
import ehealth.group1.backend.helper.dataloaders.DefaultDataLoader;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Component
public class GraphicsModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final int lead_num = 1;
    private final int lead_y_size;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    private DefaultDataLoader dataLoader;
    private GraphicsSettings gSettings;

    private double titleBar_centerX;
    private double titleBar_centerY;
    private double titleBar_halfWidth;
    private double titleBar_halfHeight;

    public GraphicsModule(DefaultDataLoader dataLoader) {
        this.dataLoader = dataLoader;
        this.gSettings = dataLoader.getGraphicsSettings();
        StdDraw.setCanvasSize(gSettings.getCanvas_x_size(), gSettings.getCanvas_y_size());
        StdDraw.setXscale(0, gSettings.getCanvas_x_size());
        StdDraw.setYscale(0, gSettings.getCanvas_y_size());
        lead_y_size = (gSettings.getCanvas_y_size() - gSettings.getTitleBarSize()) / lead_num;
        titleBar_centerX = 0.5 * gSettings.getCanvas_x_size();
        titleBar_centerY = gSettings.getCanvas_y_size() - (0.5 * gSettings.getTitleBarSize());
        titleBar_halfWidth = 0.5 * gSettings.getCanvas_x_size();
        titleBar_halfHeight = 0.5 * gSettings.getTitleBarSize();

        if(gSettings.useDoubleBuffering()) {
            StdDraw.enableDoubleBuffering();
        } else {
            StdDraw.disableDoubleBuffering();
        }
    }

    public void drawECG(List<Observation.ObservationComponentComponent> comps, LocalDateTime timestamp) {
        // Draw background
        StdDraw.clear(gSettings.getBackground());

        StdDraw.setPenColor(gSettings.getTitleBar());
        StdDraw.filledRectangle(titleBar_centerX, titleBar_centerY, titleBar_halfWidth, titleBar_halfHeight);

        // Draw component divider lines
        StdDraw.setPenColor(gSettings.getBase());
        StdDraw.setPenRadius(gSettings.getLineThickness_dividerLines());
        StdDraw.line(0, gSettings.getCanvas_y_size() - gSettings.getTitleBarSize(), gSettings.getCanvas_x_size(),
                gSettings.getCanvas_y_size() - gSettings.getTitleBarSize());

        for(int i = 1; i <= comps.size(); i++) {
            StdDraw.line(0, i*lead_y_size, gSettings.getCanvas_x_size(), i*lead_y_size);
        }

        StdDraw.setFont(gSettings.getFont_timestamp());
        StdDraw.textRight(gSettings.getCanvas_x_size() - 10, gSettings.getCanvas_y_size() - (0.5 * gSettings.getTitleBarSize()),
                dtf.format(timestamp));

        for(int i = 1; i <= comps.size(); i++) {
            drawComponent(comps.get(i - 1), i);
        }

        if(gSettings.useDoubleBuffering()) {
            StdDraw.show();
        }
    }

    private void drawComponent(Observation.ObservationComponentComponent comp, int mod) {
        StdDraw.setFont(gSettings.getFont_leadName());
        StdDraw.setPenColor(gSettings.getText());

        String s = comp.getCode().getCoding().get(0).getDisplay();
        StdDraw.textLeft(10, (mod * lead_y_size) - 20, comp.getCode().getCoding().get(0).getDisplay());

        double[] data = transformData(
                Arrays.stream(comp.getValueSampledData().getData().split(" ")).mapToDouble(Double::parseDouble).toArray(),
                mod
        );

        int datapoints = data.length;
        double datapoint_x_dist = ((double) gSettings.getCanvas_x_size() - 10) / datapoints;

        StdDraw.setPenColor(gSettings.getEcgGraph());
        StdDraw.setPenRadius(gSettings.getLineThickness_ecgGraph());
        double originMod = mod - 0.5;
        double prev_d_y = originMod * lead_y_size;

        for(int i = 0; i < datapoints; i++) {
            double d_y = data[i];

            StdDraw.line(i*datapoint_x_dist, prev_d_y, (i+1)*datapoint_x_dist, d_y);

            prev_d_y = d_y;
        }
    }

    private double[] transformData(double[] data, int iteration) {
        DoubleSummaryStatistics stats = Arrays.stream(data).summaryStatistics();
        double minVal = stats.getMin();
        double maxVal = stats.getMax();
        double spanVal = maxVal - minVal;
        double minY = ((iteration - 1) * lead_y_size);
        double maxY = (iteration * lead_y_size);
        double spanY = maxY - minY;
        minY += spanY * 0.1;
        maxY -= spanY * 0.1;
        spanY = maxY - minY;
        double spanMod = spanVal / spanY;

        for(int i = 0; i < data.length; i++) {
            data[i] = ((data[i] - minVal) / spanMod) + minY;
        }

        return data;
    }
}
