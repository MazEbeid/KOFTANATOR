import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

class MyDragDropListener implements DropTargetListener {

    @Override
    public void drop(DropTargetDropEvent event) {

        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {

            try {

                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get all of the dropped files
                    List files = (List) transferable.getTransferData(flavor);

                    for (int i = 0; i < files.size(); i++) {
                        System.out.println("File path is '" + (File)files.get(i)+"'.");
                        String [] filePath = files.get(i).toString().split("/");
                        String apkName = filePath[filePath.length-1];

                        Main.apkName.setText(apkName);
                        Main.dNdLabel.setText("Installing apk");
                       Main.dNdLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        Main.frame.repaint();
                    }
                    // Loop them through
                   /* for (File file: files) {

                        // Print out the file path


                    }*/

                }

            } catch (Exception e) {

                // Print out the error stack
                e.printStackTrace();

            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);

    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {

    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }

}