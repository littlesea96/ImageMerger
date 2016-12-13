import java.awt.Color;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Merge {
    public static double ACCEPTABLE_RANGE = 1000;

    public static double get4D_Distance(Color pixel1, Color pixel2){
        int r1 = pixel1.getRed();
        int b1 = pixel1.getBlue();
        int g1 = pixel1.getGreen();
        int a1 = pixel1.getAlpha();

        int r2 = pixel2.getRed();
        int b2 = pixel2.getBlue();
        int g2 = pixel2.getGreen();
        int a2 = pixel2.getAlpha();

        return (Math.pow(r1-r2, 2) + Math.pow(b1-b2, 2) + Math.pow(g1-g2, 2) + Math.pow(a1-a2, 2));
    }


    public static Color average_color(Color[] colors) {

        double average_r = 0;
        double average_b = 0;
        double average_g = 0;

        for (Color c:colors) {
            average_r +=  Math.pow(c.getRed(),2);
            average_b +=  Math.pow(c.getBlue(),2);
            average_g +=  Math.pow(c.getGreen(),2);
        }

        double divisor = colors.length;
        average_r = Math.sqrt(average_r/divisor);
        average_b = Math.sqrt(average_b/divisor);
        average_g = Math.sqrt(average_g/divisor);

        Color new_color = new Color( (int)average_r, (int)average_g, (int)average_b);

        return new_color;
    }

    public static int[] find_max(ArrayList<Double> keep_distance) {
        double maxVal = Collections.max(keep_distance);
        int[] indexes = new int[2];
        indexes[0] = keep_distance.indexOf(maxVal);
        for (int i = 0; i < keep_distance.size(); i++) {
            if (i != indexes[0]) {
                indexes[1] = i;
                break;
            }
        }
        return indexes;
    }

    public static void main(String[] args) throws IOException{
        long starttime = System.nanoTime();
        ArrayList<BufferedImage> buff_img = new ArrayList<>();

        String outFile = args[args.length - 1] + ".jpg";

        for (int i = 0; i < args.length - 1; i++) {
            buff_img.add(ImageIO.read(new File(args[i])));
        }

        int w = buff_img.get(0).getWidth();
        int h = buff_img.get(0).getHeight();
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

        int buff_size = buff_img.size();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++){

                ArrayList<Double> keep_Distance = new ArrayList<>();
                Color[] colors = new Color[buff_size];
                int[] pixel = new int[buff_size];

                for (int k = 0; k < buff_size; k++) {
                    pixel[k] = buff_img.get(k).getRGB(j,i);
                    colors[k] = new Color(pixel[k]);
                }

                for (int k = 0; k < buff_size; k++) {
                    double distance = 0;
                    for (int l = 0; l < buff_size; l++) {
                        if(l != k){
                            distance += get4D_Distance(colors[k], colors[l]);
                        }
                    }

                    keep_Distance.add(distance);
                }

                int[] max = find_max(keep_Distance);
                int max_index = max[0];
                int compare_index = max[1];

                if (get4D_Distance(colors[max_index], colors[compare_index]) > ACCEPTABLE_RANGE){
                    dest.setRGB(j,i,colors[max_index].getRGB());
                }else{
                    Color new_color = average_color(colors);
                    dest.setRGB(j,i,new_color.getRGB());
                }
            }
        }

        ImageIO.write(dest, "jpg", new File(outFile));
        System.out.println("Elapsed in = "+ ((System.nanoTime() - starttime) * Math.pow(10, - 9)) + " seconds");

    }


}
