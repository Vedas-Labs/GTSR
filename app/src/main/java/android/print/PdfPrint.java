package android.print;

/**
 * Created by ADMIN on 07-05-2019.
 */

import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfPrint {

    private static final String TAG = PdfPrint.class.getSimpleName();
    private final PrintAttributes printAttributes;

    public PdfPrint(PrintAttributes printAttributes) {
        this.printAttributes = printAttributes;
    }

    public void print(final PrintDocumentAdapter printAdapter, final File path, final String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            printAdapter.onLayout(null, printAttributes, null, new PrintDocumentAdapter.LayoutResultCallback() {
                @Override
                public void onLayoutFinished(final PrintDocumentInfo info, boolean changed) {
                    printAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, getOutputFile(path, fileName), new CancellationSignal(), new PrintDocumentAdapter.WriteResultCallback() {
                        @Override
                        public void onWriteFinished(PageRange[] pages) {
                            super.onWriteFinished(pages);
                        }
                    });
                }
            }, null);
        }
    }
    private ParcelFileDescriptor getOutputFile(File path, String fileName) {
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, fileName);
        try {
            file.createNewFile();
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e);
        }
        return null;
    }

}
