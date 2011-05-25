package com.larvalabs.svgandroid.util;

import android.graphics.Matrix;

import com.larvalabs.svgandroid.exception.SVGParseException;
import com.larvalabs.svgandroid.util.SVGNumberParser.SVGNumberParserFloatResult;


/**
 * @author Larva Labs, LLC
 * @author Nicolas Gramlich
 * @since 16:56:54 - 21.05.2011
 */
public class SVGTransformParser {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static Matrix parseTransform(final String pString) {
		if(pString == null) {
			return null;
		}
		
		/* TODO SVG allows transformations like this:
		 * transform="translate(-10,-20) scale(2) rotate(45) translate(5,10)" 
		 */

		try {
			if (pString.startsWith("matrix(")) {
				return SVGTransformParser.parseTransformMatrix(pString);
			} else if (pString.startsWith("translate(")) {
				return SVGTransformParser.parseTransformTranslate(pString);
			} else if (pString.startsWith("scale(")) {
				return SVGTransformParser.parseTransformScale(pString);
			} else if (pString.startsWith("skewX(")) {
				return SVGTransformParser.parseTransformSkewX(pString);
			} else if (pString.startsWith("skewY(")) {
				return SVGTransformParser.parseTransformSkewY(pString);
			} else if (pString.startsWith("rotate(")) {
				return SVGTransformParser.parseTransformRotate(pString);
			} else {
				throw new SVGParseException("Unexpected transform type: '" + pString + "'.");
			}
		} catch (final SVGParseException e) {
			throw new SVGParseException("Could not parse transform: '" + pString + "'.", e);
		}
	}

	public static Matrix parseTransformRotate(final String pString) {
		final SVGNumberParserFloatResult svgNumberParserFloatResult = SVGNumberParser.parseFloats(pString.substring("rotate(".length(), pString.indexOf(')')));
		SVGTransformParser.assertNumberParserResultNumberCountMinimum(svgNumberParserFloatResult, 1);

		final float angle = svgNumberParserFloatResult.getNumber(0);
		float cx = 0;
		float cy = 0;
		if (svgNumberParserFloatResult.getNumberCount() > 2) {
			cx = svgNumberParserFloatResult.getNumber(1);
			cy = svgNumberParserFloatResult.getNumber(2);
		}
		final Matrix matrix = new Matrix();
		matrix.postTranslate(cx, cy);
		matrix.postRotate(angle);
		matrix.postTranslate(-cx, -cy);
		return matrix;
	}

	public static Matrix parseTransformSkewY(final String pString) {
		final SVGNumberParserFloatResult svgNumberParserFloatResult = SVGNumberParser.parseFloats(pString.substring("skewY(".length(), pString.indexOf(')')));
		SVGTransformParser.assertNumberParserResultNumberCountMinimum(svgNumberParserFloatResult, 1);

		final float angle = svgNumberParserFloatResult.getNumber(0);
		final Matrix matrix = new Matrix();
		matrix.postSkew(0, (float) Math.tan(angle));
		return matrix;
	}

	static Matrix parseTransformSkewX(final String pString) {
		final SVGNumberParserFloatResult svgNumberParserFloatResult = SVGNumberParser.parseFloats(pString.substring("skewX(".length(), pString.indexOf(')')));
		SVGTransformParser.assertNumberParserResultNumberCountMinimum(svgNumberParserFloatResult, 1);

		final float angle = svgNumberParserFloatResult.getNumber(0);
		final Matrix matrix = new Matrix();
		matrix.postSkew((float) Math.tan(angle), 0);
		return matrix;
	}

	public static Matrix parseTransformScale(final String pString) {
		final SVGNumberParserFloatResult svgNumberParserFloatResult = SVGNumberParser.parseFloats(pString.substring("scale(".length(), pString.indexOf(')')));
		SVGTransformParser.assertNumberParserResultNumberCountMinimum(svgNumberParserFloatResult, 1);
		final float sx = svgNumberParserFloatResult.getNumber(0);
		float sy = 0;
		if (svgNumberParserFloatResult.getNumberCount() > 1) {
			sy = svgNumberParserFloatResult.getNumber(1);
		}
		final Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);
		return matrix;
	}

	public static Matrix parseTransformTranslate(final String pString) {
		final SVGNumberParserFloatResult svgNumberParserFloatResult = SVGNumberParser.parseFloats(pString.substring("translate(".length(), pString.indexOf(')')));
		SVGTransformParser.assertNumberParserResultNumberCountMinimum(svgNumberParserFloatResult, 1);
		final float tx = svgNumberParserFloatResult.getNumber(0);
		float ty = 0;
		if (svgNumberParserFloatResult.getNumberCount() > 1) {
			ty = svgNumberParserFloatResult.getNumber(1);
		}
		final Matrix matrix = new Matrix();
		matrix.postTranslate(tx, ty);
		return matrix;
	}

	public static Matrix parseTransformMatrix(final String pString) {
		final SVGNumberParserFloatResult svgNumberParserFloatResult = SVGNumberParser.parseFloats(pString.substring("matrix(".length(), pString.indexOf(')')));
		SVGTransformParser.assertNumberParserResultNumberCount(svgNumberParserFloatResult, 6);
		final Matrix matrix = new Matrix();
		matrix.setValues(new float[]{
				// Row 1
				svgNumberParserFloatResult.getNumber(0),
				svgNumberParserFloatResult.getNumber(2),
				svgNumberParserFloatResult.getNumber(4),
				// Row 2
				svgNumberParserFloatResult.getNumber(1),
				svgNumberParserFloatResult.getNumber(3),
				svgNumberParserFloatResult.getNumber(5),
				// Row 3
				0,
				0,
				1,
		});
		return matrix;
	}

	private static void assertNumberParserResultNumberCountMinimum(final SVGNumberParserFloatResult pSVGNumberParserFloatResult, final int pNumberParserResultNumberCountMinimum) {
		final int svgNumberParserFloatResultNumberCount = pSVGNumberParserFloatResult.getNumberCount();
		if(svgNumberParserFloatResultNumberCount < pNumberParserResultNumberCountMinimum) {
			throw new SVGParseException("Not enough data. Minimum Expected: '" + pNumberParserResultNumberCountMinimum + "'. Actual: '" + svgNumberParserFloatResultNumberCount + "'.");
		}
	}

	private static void assertNumberParserResultNumberCount(final SVGNumberParserFloatResult pSVGNumberParserFloatResult, final int pNumberParserResultNumberCount) {
		final int svgNumberParserFloatResultNumberCount = pSVGNumberParserFloatResult.getNumberCount();
		if(svgNumberParserFloatResultNumberCount != pNumberParserResultNumberCount) {
			throw new SVGParseException("Unexpected number count. Expected: '" + pNumberParserResultNumberCount + "'. Actual: '" + svgNumberParserFloatResultNumberCount + "'.");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
