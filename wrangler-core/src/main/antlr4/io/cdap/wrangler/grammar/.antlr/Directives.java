// Generated from /home/akshaj/wrangler/wrangler-core/src/main/antlr4/io/cdap/wrangler/grammar/Directives.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class Directives extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BYTE_SIZE=1, TIME_DURATION=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BYTE_SIZE", "TIME_DURATION", "BYTE_UNIT", "TIME_UNIT", "INT_PART", "DEC_PART"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BYTE_SIZE", "TIME_DURATION"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public Directives(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Directives.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0002>\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0003\u0000\u0011\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u0018\b\u0001\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002%\b\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		".\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u00043\b\u0004\n\u0004"+
		"\f\u00046\t\u0004\u0003\u00048\b\u0004\u0001\u0005\u0004\u0005;\b\u0005"+
		"\u000b\u0005\f\u0005<\u0000\u0000\u0006\u0001\u0001\u0003\u0002\u0005"+
		"\u0000\u0007\u0000\t\u0000\u000b\u0000\u0001\u0000\b\u0002\u0000BBbb\u0002"+
		"\u0000KKkk\u0002\u0000MMmm\u0002\u0000GGgg\u0002\u0000TTtt\u0003\u0000"+
		"hhmmss\u0001\u000019\u0001\u000009E\u0000\u0001\u0001\u0000\u0000\u0000"+
		"\u0000\u0003\u0001\u0000\u0000\u0000\u0001\r\u0001\u0000\u0000\u0000\u0003"+
		"\u0014\u0001\u0000\u0000\u0000\u0005$\u0001\u0000\u0000\u0000\u0007-\u0001"+
		"\u0000\u0000\u0000\t7\u0001\u0000\u0000\u0000\u000b:\u0001\u0000\u0000"+
		"\u0000\r\u0010\u0003\t\u0004\u0000\u000e\u000f\u0005.\u0000\u0000\u000f"+
		"\u0011\u0003\u000b\u0005\u0000\u0010\u000e\u0001\u0000\u0000\u0000\u0010"+
		"\u0011\u0001\u0000\u0000\u0000\u0011\u0012\u0001\u0000\u0000\u0000\u0012"+
		"\u0013\u0003\u0005\u0002\u0000\u0013\u0002\u0001\u0000\u0000\u0000\u0014"+
		"\u0017\u0003\t\u0004\u0000\u0015\u0016\u0005.\u0000\u0000\u0016\u0018"+
		"\u0003\u000b\u0005\u0000\u0017\u0015\u0001\u0000\u0000\u0000\u0017\u0018"+
		"\u0001\u0000\u0000\u0000\u0018\u0019\u0001\u0000\u0000\u0000\u0019\u001a"+
		"\u0003\u0007\u0003\u0000\u001a\u0004\u0001\u0000\u0000\u0000\u001b%\u0007"+
		"\u0000\u0000\u0000\u001c\u001d\u0007\u0001\u0000\u0000\u001d%\u0007\u0000"+
		"\u0000\u0000\u001e\u001f\u0007\u0002\u0000\u0000\u001f%\u0007\u0000\u0000"+
		"\u0000 !\u0007\u0003\u0000\u0000!%\u0007\u0000\u0000\u0000\"#\u0007\u0004"+
		"\u0000\u0000#%\u0007\u0000\u0000\u0000$\u001b\u0001\u0000\u0000\u0000"+
		"$\u001c\u0001\u0000\u0000\u0000$\u001e\u0001\u0000\u0000\u0000$ \u0001"+
		"\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000%\u0006\u0001\u0000\u0000"+
		"\u0000&\'\u0005n\u0000\u0000\'.\u0005s\u0000\u0000()\u0005u\u0000\u0000"+
		").\u0005s\u0000\u0000*+\u0005m\u0000\u0000+.\u0005s\u0000\u0000,.\u0007"+
		"\u0005\u0000\u0000-&\u0001\u0000\u0000\u0000-(\u0001\u0000\u0000\u0000"+
		"-*\u0001\u0000\u0000\u0000-,\u0001\u0000\u0000\u0000.\b\u0001\u0000\u0000"+
		"\u0000/8\u00050\u0000\u000004\u0007\u0006\u0000\u000013\u0007\u0007\u0000"+
		"\u000021\u0001\u0000\u0000\u000036\u0001\u0000\u0000\u000042\u0001\u0000"+
		"\u0000\u000045\u0001\u0000\u0000\u000058\u0001\u0000\u0000\u000064\u0001"+
		"\u0000\u0000\u00007/\u0001\u0000\u0000\u000070\u0001\u0000\u0000\u0000"+
		"8\n\u0001\u0000\u0000\u00009;\u0007\u0007\u0000\u0000:9\u0001\u0000\u0000"+
		"\u0000;<\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000<=\u0001\u0000"+
		"\u0000\u0000=\f\u0001\u0000\u0000\u0000\b\u0000\u0010\u0017$-47<\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}