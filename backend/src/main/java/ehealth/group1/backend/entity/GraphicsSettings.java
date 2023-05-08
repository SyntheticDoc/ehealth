package ehealth.group1.backend.entity;

import lombok.*;

import java.awt.*;

@NoArgsConstructor
@Getter @Setter
public class GraphicsSettings {
    private int canvas_x_size, canvas_y_size;
    private int titleBarSize;
    private double lineThickness_dividerLines;
    private double lineThickness_ecgGraph;
    private Color background;
    private Color base;
    private Color ecgGraph;
    private Color text;
    private Font font_leadName;
    private Font font_timestamp;

    @Getter(AccessLevel.NONE)
    private boolean useDoubleBuffering;

    public boolean useDoubleBuffering() {
        return useDoubleBuffering;
    }
}
