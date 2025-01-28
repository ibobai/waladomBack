package com.phanta.waladom.utiles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class CountryCodeUtil {

    private static final Map<String, String> COUNTRY_CODES;

    static {
        Map<String, String> codes = new HashMap<>();
        codes.put("AFGHANISTAN", "93");
        codes.put("ALBANIA", "355");
        codes.put("ALGERIA", "213");
        codes.put("ANDORRA", "376");
        codes.put("ANGOLA", "244");
        codes.put("ARGENTINA", "54");
        codes.put("ARMENIA", "374");
        codes.put("AUSTRALIA", "61");
        codes.put("AUSTRIA", "43");
        codes.put("AZERBAIJAN", "994");
        codes.put("BAHRAIN", "973");
        codes.put("BANGLADESH", "880");
        codes.put("BELGIUM", "32");
        codes.put("BELARUS", "375");
        codes.put("BRAZIL", "55");
        codes.put("BULGARIA", "359");
        codes.put("CANADA", "1");
        codes.put("CHINA", "86");
        codes.put("COLOMBIA", "57");
        codes.put("CROATIA", "385");
        codes.put("CUBA", "53");
        codes.put("CZECH REPUBLIC", "420");
        codes.put("DENMARK", "45");
        codes.put("EGYPT", "20");
        codes.put("ESTONIA", "372");
        codes.put("ETHIOPIA", "251");
        codes.put("FINLAND", "358");
        codes.put("FRANCE", "33");
        codes.put("GERMANY", "49");
        codes.put("GHANA", "233");
        codes.put("GREECE", "30");
        codes.put("HUNGARY", "36");
        codes.put("ICELAND", "354");
        codes.put("INDIA", "91");
        codes.put("INDONESIA", "62");
        codes.put("IRAN", "98");
        codes.put("IRAQ", "964");
        codes.put("IRELAND", "353");
        codes.put("ISRAEL", "972");
        codes.put("ITALY", "39");
        codes.put("JAPAN", "81");
        codes.put("JORDAN", "962");
        codes.put("KENYA", "254");
        codes.put("KUWAIT", "965");
        codes.put("LEBANON", "961");
        codes.put("LIBYA", "218");
        codes.put("LUXEMBOURG", "352");
        codes.put("MALAYSIA", "60");
        codes.put("MEXICO", "52");
        codes.put("MOROCCO", "212");
        codes.put("NETHERLANDS", "31");
        codes.put("NEW ZEALAND", "64");
        codes.put("NIGERIA", "234");
        codes.put("NORWAY", "47");
        codes.put("PAKISTAN", "92");
        codes.put("PERU", "51");
        codes.put("PHILIPPINES", "63");
        codes.put("POLAND", "48");
        codes.put("PORTUGAL", "351");
        codes.put("QATAR", "974");
        codes.put("ROMANIA", "40");
        codes.put("RUSSIA", "7");
        codes.put("SAUDI ARABIA", "966");
        codes.put("SINGAPORE", "65");
        codes.put("SOUTH AFRICA", "27");
        codes.put("SOUTH KOREA", "82");
        codes.put("SPAIN", "34");
        codes.put("SUDAN", "249");
        codes.put("SWEDEN", "46");
        codes.put("SWITZERLAND", "41");
        codes.put("SYRIA", "963");
        codes.put("THAILAND", "66");
        codes.put("TUNISIA", "216");
        codes.put("TURKEY", "90");
        codes.put("UKRAINE", "380");
        codes.put("UNITED ARAB EMIRATES", "971");
        codes.put("UNITED KINGDOM", "44");
        codes.put("UNITED STATES", "1");
        codes.put("URUGUAY", "598");
        codes.put("UZBEKISTAN", "998");
        codes.put("VENEZUELA", "58");
        codes.put("VIETNAM", "84");
        codes.put("YEMEN", "967");
        codes.put("ZAMBIA", "260");
        codes.put("ZIMBABWE", "263");

        COUNTRY_CODES = Collections.unmodifiableMap(codes);
    }

    public static String getCountryCode(String countryName) {
        String code = COUNTRY_CODES.get(countryName.toUpperCase());
        if (code == null) {
            throw new IllegalArgumentException("Unknown country: " + countryName);
        }
        return code;
    }

    public static Map<String, String> getAllCountryCodes() {
        return new HashMap<>(COUNTRY_CODES); // Return a copy to prevent modifications
    }

    public static void main(String[] args) {
        System.out.println("Sudan: " + getCountryCode("SUDAN"));  // Output: 249
        System.out.println("France: " + getCountryCode("FRANCE"));  // Output: 33

        System.out.println("\nAll Countries and Codes:");
        getAllCountryCodes().forEach((country, code) ->
                System.out.println(country + " -> " + code)
        );
    }
}
