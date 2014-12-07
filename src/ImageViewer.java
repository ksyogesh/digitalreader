// Programming Graphical User Interfaces 2002
   // Example Program
   // Jarkko Leponiemi
   import java.awt.*;
   import java.awt.event.*;
   import java.io.File;
   import java.net.URL;
   import javax.swing.*;
import javax.swing.filechooser.*;
  
  /**
  * An application used for viewing images in disk files.
  */
  public class ImageViewer extends JFrame
  {
  
    public static final String APPNAME = "ImageViewer";
    Image image;
    boolean scaled;
    public ImageViewer()
    {
    	//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1280, 700);
      JMenuBar mbar = new JMenuBar();
      JMenu menu = new JMenu("File");
      menu.add(new LoadImageAction());
      menu.addSeparator();
     menu.add(new ExitAction());
      mbar.add(menu);
      setJMenuBar(mbar);
      
      setTitle(APPNAME);
      
      imageComp = new ImageComponent(image, scaled);
      if (scaled)
        getContentPane().add(imageComp);
      else
        getContentPane().add(new JScrollPane(imageComp));
    }
    
    private ImageComponent imageComp = null;
    
    // ImageComponent is used for displaying the image
    protected class ImageComponent extends JComponent
    {
      private Image image = null;
      private boolean scaled = false;
      private Dimension size = null;
      private Insets insets = new Insets(0, 0, 0, 0);
      
      ImageComponent(Image image, boolean scaled)
      {
        this.image = image;
        this.scaled = scaled;
      }
      
      void setImage(Image image)
      {
        this.image = image;
        revalidate();
      }
     
      public void paint(Graphics g)
      {
        super.paint(g);
        insets = getInsets(insets);
        size = getSize(size);
        if (image == null)
          return;
        if (scaled)
          g.drawImage(image, 
        insets.left, insets.top, 
        size.width - insets.left - insets.right,
        size.height - insets.top - insets.bottom,
        this);
        else
          g.drawImage(image, insets.left, insets.top, this);
      }
      
      public Dimension getMinimumSize()
      {
        int imgw = 32, imgh = 32;
        if (image != null)
        {
          imgw = image.getWidth(this);
          imgh = image.getHeight(this);
        }
        insets = getInsets(insets);
        return new Dimension(
        insets.left + Math.max(32, imgw / 10) + insets.right,
        insets.top + Math.max(32, imgh / 10) + insets.bottom
        );
      }
      
      public Dimension getPreferredSize()
      {
        int imgw = 32, imgh = 32;
        if (image != null)
        {
          imgw = image.getWidth(this);
          imgh = image.getHeight(this);
       }
insets = getInsets(insets);
       return new Dimension(
       insets.left + imgw + insets.right,
       insets.top + imgh + insets.bottom
       );
     }
    
     public Dimension getMaximumSize() { return getPreferredSize(); }
 }
  
   // An action class for opening a new image
   protected class LoadImageAction extends AbstractAction
   {
     LoadImageAction()
     {
       putValue(NAME, "Load Image...");
       putValue(SHORT_DESCRIPTION, "Load an image file from the local discs.");
     }
    
     public void actionPerformed(ActionEvent e)
     {
JFileChooser chooser = new JFileChooser();
       FileFilter filter = new ExtensionFileFilter(
         new String[] {"gif", "GIF"}, "GIF image files");
       chooser.addChoosableFileFilter(filter);
       filter = new ExtensionFileFilter(
         new String[] {"jpg", "JPG", "jpeg", "JPEG"}, "JPG image files");
       chooser.addChoosableFileFilter(filter);
       filter = new ExtensionFileFilter(
         new String[] {"gif", "GIF", "jpg", "JPG", "jpeg", "JPEG"}, "GIF and JPG image files");
       chooser.addChoosableFileFilter(filter);
       chooser.removeChoosableFileFilter(
       chooser.getAcceptAllFileFilter());
       int returnVal = chooser.showOpenDialog(ImageViewer.this);
       if(returnVal == JFileChooser.APPROVE_OPTION)
       {
         String path = chooser.getSelectedFile().getPath();
         System.out.println(path);
         Image image = Toolkit.getDefaultToolkit().getImage(path);
         if (image != null)
         {
           imageComp.setImage(image);
           setTitle(APPNAME + ": " + path);
         }
       }
     }
   }
   
   // file filter for opening files with a specified extension
   protected class ExtensionFileFilter extends FileFilter
   {
     ExtensionFileFilter(String[] extensions, String description)
     {
       this.extensions = extensions;
       this.description = description;
     }
     
     public boolean accept(File f)
     {
       if (f.isDirectory())
         return true;
       
       String name = f.getName().toUpperCase();
       for (int i = 0; i < extensions.length; i++) 
         if (name.endsWith("." + extensions[i]))
           return true;
       return false;
     }
     
     public String getDescription() { return description; }
     
     private String[] extensions;
     private String description;
   }
   
   protected class ExitAction extends AbstractAction
   {
     ExitAction() { putValue(NAME, "Exit"); }
     
     public void actionPerformed(ActionEvent e) { System.exit(0); }
   }
   
   public static void main(String[] args)
   {
     String title = APPNAME;
     String ifilename = null;
     Image image = null;
     boolean scaled = false;
     try {
       for (int i = 0; i < args.length; i++)
       {
         if (args[i].equals("-scaled"))
           scaled = true;
         else if (args[i].equals("-file"))
         {
           ifilename = args[++i];
           image = Toolkit.getDefaultToolkit().getImage(ifilename);
         }
         else if (args[i].equals("-url"))
         {
           ifilename = args[++i];
           image = Toolkit.getDefaultToolkit().getImage(new URL(ifilename));
         }
         else
           throw new Exception();
       }
     }
     catch (Exception e)
     {
       e.printStackTrace();
       System.out.println(
       "Parameters: [-scaled] [-file <file name>] [-url <URL]"
       );
       System.exit(0);
     }
    
     if (image != null)
    {
       title = title + ": " + ifilename;
     }
    
     JFrame f = new ImageViewer();
     f.addWindowListener(new WindowAdapter() {
       public void windowClosing(WindowEvent e) { System.exit(0); }
     });
     f.pack();
     f.show();
   }
  
 }
