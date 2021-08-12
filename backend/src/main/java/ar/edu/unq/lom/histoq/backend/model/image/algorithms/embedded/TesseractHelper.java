package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import com.sun.jna.StringArray;
import com.sun.jna.ptr.PointerByReference;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.util.ImageIOHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TesseractHelper {
    private String language = "eng";
    private String datapath;
    private int psm = -1;
    private int ocrEngineMode = 3;
    private final List<String> configList = new ArrayList();
    private TessAPI api;
    private ITessAPI.TessBaseAPI handle;

    public TesseractHelper(String datapath, String language, int ocrEngineMode, int psm) {
        this.datapath = datapath;
        this.language = language;
        this.ocrEngineMode = ocrEngineMode;
        this.psm = psm;
    }

    // This method returns true if Tess4j.Tesseract.getWords will fail
    // with a horrible NullPointerException that cant be caught (?).
    // Simply do the same first steps of Tess4j.Tesseract.getWords
    // and checks if this.api.TessResultIteratorGetUTF8Text(ri, pageIteratorLevel) == null...
    public boolean getWordsWillFail(BufferedImage image, int pageIteratorLevel) {
        boolean willFail = true;

        this.tesseractInit();

        try {
            this.tesseractSetImage(image, (Rectangle) null);
            this.api.TessBaseAPIRecognize(this.handle, (ITessAPI.ETEXT_DESC) null);
            ITessAPI.TessResultIterator ri = this.api.TessBaseAPIGetIterator(this.handle);
            ITessAPI.TessPageIterator pi = this.api.TessResultIteratorGetPageIterator(ri);
            this.api.TessPageIteratorBegin(pi);

            willFail = this.api.TessResultIteratorGetUTF8Text(ri, pageIteratorLevel) == null;
            this.tesseractDispose();
        }
        catch (Exception e) {
            this.tesseractDispose();
        }

        return willFail;
    }


    // Methods stolen from Tess4j.Tesseract clas...

    private void tesseractInit() {
        this.api = TessAPI.INSTANCE;
        this.handle = this.api.TessBaseAPICreate();
        StringArray var1 = new StringArray((String[])this.configList.toArray(new String[0]));
        PointerByReference var2 = new PointerByReference();
        var2.setPointer(var1);
        this.api.TessBaseAPIInit1(this.handle, this.datapath, this.language, this.ocrEngineMode, var2, this.configList.size());
        if (this.psm > -1) {
            this.api.TessBaseAPISetPageSegMode(this.handle, this.psm);
        }
    }

    private void tesseractSetImage(RenderedImage var1, Rectangle var2) throws IOException {
        ByteBuffer var3 = ImageIOHelper.getImageByteBuffer(var1);
        DataBuffer var5 = var1.getData(new Rectangle(1, 1)).getDataBuffer();
        int var4;
        if (var5 instanceof DataBufferByte) {
            var4 = var1.getColorModel().getPixelSize();
        } else {
            var4 = 8;
        }

        this.tesseractSetImage(var1.getWidth(), var1.getHeight(), var3, var2, var4);
    }

    private void tesseractSetImage(int var1, int var2, ByteBuffer var3, Rectangle var4, int var5) {
        int var6 = var5 / 8;
        int var7 = (int)Math.ceil((double)(var1 * var5) / 8.0D);
        this.api.TessBaseAPISetImage(this.handle, var3, var1, var2, var6, var7);
        if (var4 != null && !var4.isEmpty()) {
            this.api.TessBaseAPISetRectangle(this.handle, var4.x, var4.y, var4.width, var4.height);
        }
    }

    protected void tesseractDispose() {
        if (this.api != null && this.handle != null) {
            this.api.TessBaseAPIDelete(this.handle);
        }
    }
}
