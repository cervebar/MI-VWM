package fit.ctu.cz.vwm.dao;

public class Constants {

	// SOLR CORES ------------------------------------------------
	public static String IR_DEV = "http://ir-dev.lmcloud.vse.cz/solr/testing_core";
	public static String LOCALHOST = "http://localhost:8983/solr/vwm";

	// SOLR FIELDS=============================================================
	public static String id = "id";
	public static String ID_USER_DEFINED = "id_user_defined";
	public static String ID_PATH = "id_path";
	public static String name = "name";
	public static String category = "category";

	public static String CATEGORY_GROUND_TRUTH = "ground_truth";// genre given by human
	public static String CATEGORY_EXTRACTED = "extracted";// genre extracted by extraction tool
	public static String CATEGORY_NO_GENRE_EXTRACTED = "no_genre_extracted";// preextracted fields
																			// but genre not yet
																			// searched

	public static String instruments = "instruments";
	public static String song_text = "song_text";
	public static String lyrics = "lyrics";
	public static String tempo = "tempo";
	public static String GENRE = "genre";
	public static String DESCRIPTION = "description";
	public static String duration = "duration";
	public static String love_factor = "love_factor";
	public static String singing = "singing";
	public static String rhytmus = "rhytmus";
	public static String song_type = "song_type";
	public static String band_name = "band_name";

	public static String AudioFundamentalFrequencyType = "AudioFundamentalFrequencyType";
	public static String HarmonicRatio = "HarmonicRatio";
	public static String UpperLimitOfHarmonicity = "UpperLimitOfHarmonicity";
	public static String AudioPowerType = "AudioPowerType";
	public static String AudioSpectrumBasisType = "AudioSpectrumBasisType";
	public static String AudioSpectrumProjectionType = "AudioSpectrumProjectionType";
	public static String AudioSpectrumFlatnessType = "AudioSpectrumFlatnessType";
	public static String AudioSpectrumSpreadType = "AudioSpectrumSpreadType";
	public static String LogAttackTimeType = "LogAttackTimeType";
	public static String SpectralCentroidType = "SpectralCentroidType";
	public static String TemporalCentroidType = "TemporalCentroidType";
	public static String HarmonicSpectralCentroidType = "HarmonicSpectralCentroidType";
	public static String HarmonicSpectralDeviationType = "HarmonicSpectralDeviationType";
	public static String HarmonicSpectralSpreadType = "HarmonicSpectralSpreadType";
	public static String HarmonicSpectralVariationType = "HarmonicSpectralVariationType";
	public static String BackgroundNoiseLevel = "BackgroundNoiseLevel";
	public static String DcOffset = "DcOffset";
	public static String BandWidth = "BandWidth";

}
