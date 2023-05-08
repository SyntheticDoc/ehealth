package ehealth.group1.backend.helper.graphics;

import ehealth.group1.backend.entity.GraphicsSettings;
import ehealth.group1.backend.helper.dataloaders.DefaultDataLoader;
import org.hl7.fhir.r5.model.Observation;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;

public class GraphicsModule {
    private final int lead_num = 1;
    private final int lead_y_size;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    private DefaultDataLoader dataLoader;
    private GraphicsSettings gSettings;

    public GraphicsModule(DefaultDataLoader dataLoader) {
        this.dataLoader = dataLoader;
        this.gSettings = dataLoader.getGraphicsSettings();
        StdDraw.setCanvasSize(gSettings.getCanvas_x_size(), gSettings.getCanvas_y_size());
        StdDraw.setXscale(0, gSettings.getCanvas_x_size());
        StdDraw.setYscale(0, gSettings.getCanvas_y_size());
        lead_y_size = gSettings.getCanvas_y_size() / lead_num;
    }

    public void drawECG(ArrayList<Observation.ObservationComponentComponent> comps, LocalDateTime timestamp) {
        if(gSettings.useDoubleBuffering()) {
            StdDraw.enableDoubleBuffering();
        } else {
            StdDraw.disableDoubleBuffering();
        }

        // Draw background
        StdDraw.clear(Color.WHITE);

        // Draw component divider lines
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.005);
        for(int i = 1; i <= comps.size(); i++) {
            StdDraw.line(0, i*lead_y_size, gSettings.getCanvas_x_size(), i*lead_y_size);
        }

        StdDraw.setFont(gSettings.getFont_timestamp());
        StdDraw.textRight(gSettings.getCanvas_x_size() - 10, gSettings.getCanvas_y_size() + (0.5 * gSettings.getTitleBarSize()),
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
            double d_y = (originMod * lead_y_size) + data[i];

            StdDraw.line(i*datapoint_x_dist, prev_d_y, (i+1)*datapoint_x_dist, d_y);

            prev_d_y = d_y;
        }
    }

    private double[] transformData(double[] data, int iteration) {
        DoubleSummaryStatistics stats = Arrays.stream(data).summaryStatistics();
        double minVal = stats.getMin() + (stats.getMin() * 0.1);
        double maxVal = stats.getMax();
        double spanVal = maxVal - minVal;
        double minY = ((iteration - 1) * lead_y_size);
        double maxY = (iteration * lead_y_size);
        double spanY = maxY - minY;
        double spanMod = spanVal / spanY;

        for(int i = 0; i < data.length; i++) {
            data[i] = ((data[i] - minVal) / spanMod) + minY;
        }

        return data;
    }

    /*public void drawECG_old(String[] comps) {
        StdDraw.clear(Color.WHITE);
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.005);
        StdDraw.line(0, lead_y_size, gSettings.getCanvas_x_size(), lead_y_size);
        StdDraw.line(0, 2*lead_y_size, gSettings.getCanvas_y_size(), 2*lead_y_size);

        Font font = new Font("Arial", Font.BOLD, 14);
        StdDraw.setFont(font);
        StdDraw.textLeft(10, lead_y_size - 15, comps[4]);
        StdDraw.textLeft(10, (2*lead_y_size) - 15, comps[2]);
        StdDraw.textLeft(10, (3*lead_y_size) - 15, comps[0]);

        int[] dat1 = Arrays.stream(comps[1].split(" ")).mapToInt(Integer::parseInt).toArray();
        int[] dat2 = Arrays.stream(comps[3].split(" ")).mapToInt(Integer::parseInt).toArray();
        int[] dat3 = Arrays.stream(comps[5].split(" ")).mapToInt(Integer::parseInt).toArray();

        int datapoints = dat1.length;
        double datapoint_x_dist = ((double) gSettings.getCanvas_x_size() - 10) / datapoints;

        StdDraw.setPenColor(Color.red);
        StdDraw.setPenRadius(0.005);

        double prev_d1_y = 2.5*lead_y_size;
        double prev_d2_y = 1.5*lead_y_size;
        double prev_d3_y = 0.5*lead_y_size;

        for(int i = 0; i < datapoints; i++) {
            double d1_y = (2.5*lead_y_size) + ((dat1[i] - 2000)/2.0);
            double d2_y = (1.5*lead_y_size) + ((dat2[i] - 2000)/2.0);
            double d3_y = (0.5*lead_y_size) + ((dat3[i] - 2000)/2.0);

            StdDraw.line(i*datapoint_x_dist, prev_d1_y, (i+1)*datapoint_x_dist, d1_y);
            StdDraw.line(i*datapoint_x_dist, prev_d2_y, (i+1)*datapoint_x_dist, d2_y);
            StdDraw.line(i*datapoint_x_dist, prev_d3_y, (i+1)*datapoint_x_dist, d3_y);

            prev_d1_y = d1_y;
            prev_d2_y = d2_y;
            prev_d3_y = d3_y;
        }

        StdDraw.show();
    }*/
}
