package icons;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * creates {@link Icon}s and {@link Image}s from the files that reside in its directory todo add cache?
 * 
 * @author eav 2009
 */
public class IMG
{
    public static final String CONFIGURE_PNG                  = "configure.png";
    public static final String DOCUMENT_REVERT_PNG            = "document-revert.png";
    public static final String BOOK_NEW                       = "address-book-new.png";
    public static final String LIST_ADD_PNG                   = "list-add.png";
    public static final String LIST_REMOVE_PNG                = "list-remove.png";
    public static final String DOCUMENT_OPEN_FOLDER_PNG       = "document-open-folder.png";
    public static final String DOCUMENT_PREVIEW_PNG           = "document-preview.png";
    public static final String HELP_ABOUT_PNG                 = "help-about.png";
    public static final String BOLD                 = "Bold16.gif";
    public static final String DOCUMENT_SAVE_PNG              = "document-save.png";
    public static final String DOCUMENT_IMPORT_PNG            = "document-import.png";
    public static final String GOOGLE_PNG                     = "google.png";
    public static final String DOCUMENT_PROPERTIES_PNG        = "document-properties.png";
    public static final String EDIT_CLEAR_LOCATIONBAR_RTL_PNG = "locationbar-erase.png";
    public static final String EDIT_FIND_PNG                  = "edit-find.png";
    public static final String ITALIC             = "Italic16.gif";
    public static final String USER_IDENITY_PNG               = "user-identity.png";
    public static final String BOOK_PNG                       = "flip1.jpg";
    public static final String SPLASH_PNG                     = "logo.png";
    public static final String LOGO_PNG                       = "logo.png";

    public static final String PREVIOUS_PNG                   = "go-previous-view.png";
    public static final String NEXT_PNG                       = "go-next-view.png";
    public static final String HOME_PNG                       = "go-home.png";
    public static final String LAST_PNG                       = "go-last-view.png";
    public static final String FIRST_PNG                      = "go-first-view.png";

    public static final String RIGHT_NEW_PNG                  = "view-right-new.png";
    public static final String RIGHT_CLOSE_PNG                = "view-right-close.png";
    public static final String ZOOM_IN_PNG                    = "zoom-in.png";
    public static final String ZOOM_OUT_PNG                   = "zoom-out.png";

    public static final String ARROW_UP_PNG                   = "arrow-up.png";
    public static final String ARROW_DOWN_PNG                 = "arrow-down.png";

    public static final String KTIP_PNG                       = "ktip.png";

    public static final String BOOKMARKS_PNG                  = "bookmarks.png";
    public static final String KNOTES_PNG                     = "knotes.png";
    public static final String VIEW_PREVIEW_PNG               = "view-preview.png";
    public static final String TOC_PNG                        = "format-justify-left.png";
    public static final String FONT_PNG                       = "font.png";
    public static final String EXTRACT_TEXT_PNG               = "edit-select-all.png";
    public static final String CLIPBOARD_PNG                  = "klipper.png";

    public static Icon icon(
        final String fileName )
    {
        return icon( fileName, 32 );
    }

    public static ImageIcon icon(
        final String fileName,
        final int size )
    {
        return new ImageIcon( img( fileName, size ) );
    }

    public static Image img(
        final String fileName,
        final int size )
    {
        final URL url = IMG.class.getResource( size + "/" + fileName );
        return Toolkit.getDefaultToolkit().getImage( url );
    }
}
