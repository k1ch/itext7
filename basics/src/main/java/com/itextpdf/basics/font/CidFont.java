package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;

import java.util.HashMap;
import java.util.StringTokenizer;

public class CidFont extends FontProgram {

    private IntHashtable hMetrics;
    private IntHashtable vMetrics;
    private int pdfFontFlags;

    public CidFont(String fontName) {
        initializeCidFontNameAndStyle(fontName);
        HashMap<String, Object> fontDesc = CidFontProperties.getAllFonts().get(fontNames.getFontName());
        if (fontDesc == null) {
            throw new PdfException("no.such.predefined.font.1").setMessageParams(fontName);
        }
        initializeCidFontProperties(fontDesc);
    }

    //TODO describe supported font properties, so that user could create his own custom cid font.
    public CidFont(String fontName, HashMap<String, Object> fontDescription) {
        initializeCidFontNameAndStyle(fontName);
        initializeCidFontProperties(fontDescription);
    }

    /**
     * Checks if the font with the given name and encoding is one
     * of the predefined CID fonts.
     * @param fontName the font name.
     * @param cmap the encoding.
     * @return {@code true} if it is CJKFont.
     */
    public static boolean isCidFont(String fontName, String cmap) {
        return FontCache.isCidFont(fontName, cmap);
    }

    public IntHashtable getHMetrics() {
        return hMetrics;
    }

    public IntHashtable getVMetrics() {
        return vMetrics;
    }

    public void setHMetrics(IntHashtable hMetrics) {
        this.hMetrics = hMetrics;
    }

    public void setVMetrics(IntHashtable vMetrics) {
        this.vMetrics = vMetrics;
    }

    @Override
    public int getWidth(int ch) {
        return hMetrics.get(ch);
    }

    @Override
    public int getKerning(int char1, int char2) {
        return 0;
    }

    @Override
    public int getPdfFontFlags() {
        return pdfFontFlags;
    }

    @Override
    protected int getRawWidth(int c, String name) {
        throw new IllegalStateException();
    }

    @Override
    protected int[] getRawCharBBox(int c, String name) {
        throw new IllegalStateException();
    }

    private void initializeCidFontNameAndStyle(String fontName) {
        String nameBase = getBaseName(fontName);
        if (nameBase.length() < fontName.length()) {
            fontNames.setFontName(fontName);
            fontNames.setStyle(fontName.substring(nameBase.length()));
        } else {
            fontNames.setFontName(fontName);
        }
    }

    private void initializeCidFontProperties(HashMap<String, Object> fontDesc) {
        setHMetrics((IntHashtable) fontDesc.get("W"));
        setVMetrics((IntHashtable) fontDesc.get("W2"));
        fontIdentification.setPanose((String) fontDesc.get("Panose"));
        fontMetrics.setItalicAngle(Integer.parseInt((String) fontDesc.get("ItalicAngle")));
        fontMetrics.setCapHeight(Integer.parseInt((String) fontDesc.get("CapHeight")));
        fontMetrics.setTypoAscender(Integer.parseInt((String) fontDesc.get("Ascent")));
        fontMetrics.setTypoDescender(Integer.parseInt((String) fontDesc.get("Descent")));
        fontMetrics.setStemV(Integer.parseInt((String) fontDesc.get("StemV")));
        pdfFontFlags = Integer.parseInt((String) fontDesc.get("Flags"));
        String fontBBox = (String) fontDesc.get("FontBBox");
        StringTokenizer tk = new StringTokenizer(fontBBox, " []\r\n\t\f");
        Integer llx = Integer.parseInt(tk.nextToken());
        Integer lly = Integer.parseInt(tk.nextToken());
        Integer urx = Integer.parseInt(tk.nextToken());
        Integer ury = Integer.parseInt(tk.nextToken());
        fontMetrics.updateBbox(llx, lly, urx, ury);
        registry = (String) fontDesc.get("Registry");
    }
}
