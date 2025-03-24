import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Unstitcher {
   
   public static void main(String[] args) throws IOException {
      
      List<String> files = new ArrayList<String>();
      List<String> filesname = new ArrayList<String>();

      // find all files in folder and find duplicate names based on files type     
      File folder = new File("./");

      for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
        } else {
            String filetmp = fileEntry.getName();
            if (Objects.equals(Unstitcher.getFileExtension(filetmp), "config")) {
               files.add(filetmp.substring(0, filetmp.length() - 7));
            } else if (Objects.equals(Unstitcher.getFileExtension(filetmp), "png")) {
               if (files.contains(filetmp.substring(0, filetmp.length() - 4))) {
                  filesname.add(filetmp.substring(0, filetmp.length() - 4));  
               }
            }
         }
      }
      
      unstich(filesname);
   }
      
   // unstiching loop per file set
   public static void unstich(List<String> names) throws IOException {
      
      int[] change = {0};
      int[] newfile = {0};
      int count = 0;
      
      for (int x = 0; x < names.size(); x++) {
      
         String line = null;
      
         BufferedImage img = ImageIO.read(new File(names.get(x) + ".png"));
         BufferedReader config = new BufferedReader(new FileReader(names.get(x) + ".config"));
         
         System.out.println(names.get(x) + ".config being read");  
      
         int spritesize = img.getHeight()/16;
      
         // firstline directory create
         String[] firstline = config.readLine().split("=");
         String dir = "." + firstline[1].substring(0, firstline[1].length() - 1);
         String dirhold = dir;

         File theDir = new File(dir);
         theDir.mkdirs();
         
         count++;
         System.out.println("line " + count + " read");  
      
         // unstiching
         while ((line = config.readLine()) != null) {
            if ((line.length() < 4) || (line.charAt(0) == '*')) {
               count++;
               System.out.println("line " + count +" skiped");  
            }
            else if ((line.charAt(0) == '-')) {
            
               count++;
               System.out.println("line " + count + " read");
                           
               // split line
               String[] tmpline = line.split("=");
               String[] tmppath = tmpline[1].split("/");
               String[] rcstring = tmpline[0].split(",");
               String[] rcstringstart = rcstring[0].split("\\.");
               String[] rcstringend = rcstring[1].split("\\.");
            
               // setup locations
               int[] rc = {((Integer.parseInt(rcstringstart[0].substring(1, rcstringstart[0].length())) - 1) * spritesize),
                  ((Integer.parseInt(rcstringstart[1]) - 1) * spritesize)};
               int[] hw = {(Integer.parseInt(rcstringend[0]) * spritesize), (Integer.parseInt(rcstringend[1]) * spritesize)};
               
               output(dir, rc, tmppath, img, (hw[1]-rc[1]), (hw[0]-rc[0]), change, newfile);
               
            } else {
            
               count++;
               System.out.println("line " + count + " read");
            
               // split line
               String[] tmpline = line.split("=");
               String[] tmppath = tmpline[1].split("/");
               String[] rcstring = tmpline[0].split("\\.");
            
               // setup locations
               int[] rc = {((Integer.parseInt(rcstring[0]) - 1) * spritesize), ((Integer.parseInt(rcstring[1]) - 1) * spritesize)};
               
               output(dir, rc, tmppath, img, spritesize, spritesize, change, newfile);
            }
         }
         count = 0; 
      }
      
      System.out.println(change[0] + " change(s)");
      System.out.println(newfile[0] + " new file(s)");
   }
   
   // output function
   public static void output(String dir, int[] rc, String[] path, BufferedImage img, int spriteh, int spritew, int[] change, int[] newfile) throws IOException {
   
      // output folder(s)
      for (int i = 0; i < (path.length - 1); i++) {
         dir = dir + "/" + path[i];
      }
           
      File theSubDir = new File(dir);
      theSubDir.mkdirs();
           
      // setup output image for transparency
      BufferedImage tmpimg = new BufferedImage(spriteh, spritew, Transparency.BITMASK);

      // split and output images
      Graphics2D sprite = tmpimg.createGraphics();
      sprite.drawImage(img.getSubimage(rc[1], rc[0], spriteh, spritew),0,0, null);
     
      // does exsist
      boolean check = new File(dir, path[path.length - 1] + ".png").exists();
     
      if(check) {
         BufferedImage isthere = ImageIO.read(new File(dir + "/" + path[path.length - 1] + ".png"));
         if (bufferedImagesEqual(isthere, tmpimg)) {
            // no change
            System.out.println("no change");
         } else {
            // changed
            ImageIO.write(tmpimg, "png", new File(dir + "/" + path[path.length - 1] + ".png"));
            System.out.println("change");
            change[0]++;
         }
      } else {
         // new file
         ImageIO.write(tmpimg, "png", new File(dir + "/" + path[path.length - 1] + ".png"));
         System.out.println("new file");
         newfile[0]++;
      }
   }
   
   // get file type
   public static String getFileExtension(String fullName) {
      String fileName = new File(fullName).getName();
      int dotIndex = fileName.lastIndexOf('.');
      return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
   }
   
   // is buffered image same
   public static boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
      if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
         for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
               if (img1.getRGB(x, y) != img2.getRGB(x, y))
                  return false;
            }
         }
      } else {
         return false;
      }
      return true;
   }
}