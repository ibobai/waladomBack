package com.phanta.waladom.utiles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class CountryCodeUtil {

    private static final Map<String, String> COUNTRY_CODES;

    static {
        Map<String, String> codes = new HashMap<>();
        codes.put("Afghanistan", "93");
        codes.put("Aland islands", "358");
        codes.put("Albania", "355");
        codes.put("Algeria", "213");
        codes.put("American samoa", "1-684");
        codes.put("Andorra", "376");
        codes.put("Angola", "244");
        codes.put("Anguilla", "1-264");
        codes.put("Antarctica", "672");
        codes.put("Antigua and barbuda", "1-268");
        codes.put("Argentina", "54");
        codes.put("Armenia", "374");
        codes.put("Aruba", "297");
        codes.put("Australia", "61");
        codes.put("Austria", "43");
        codes.put("Azerbaijan", "994");
        codes.put("Bahamas", "1-242");
        codes.put("Bahrain", "973");
        codes.put("Bangladesh", "880");
        codes.put("Barbados", "1-246");
        codes.put("Belarus", "375");
        codes.put("Belgium", "32");
        codes.put("Belize", "501");
        codes.put("Benin", "229");
        codes.put("Bermuda", "1-441");
        codes.put("Bhutan", "975");
        codes.put("Bolivia", "591");
        codes.put("Bosnia and herzegovina", "387");
        codes.put("Botswana", "267");
        codes.put("Bouvet island", "none"); // Bouvet Island doesn't have a phone code.
        codes.put("Brazil", "55");
        codes.put("British indian ocean territory", "246");
        codes.put("Brunei darussalam", "673");
        codes.put("Bulgaria", "359");
        codes.put("Burkina faso", "226");
        codes.put("Burundi", "257");
        codes.put("Cambodia", "855");
        codes.put("Cameroon", "237");
        codes.put("Canada", "1");
        codes.put("Cape verde", "238");
        codes.put("Cayman islands", "1-345");
        codes.put("Central african republic", "236");
        codes.put("Chad", "235");
        codes.put("Chile", "56");
        codes.put("China", "86");
        codes.put("Christmas island", "61");
        codes.put("Cocos islands", "61");
        codes.put("Colombia", "57");
        codes.put("Comoros", "269");
        codes.put("Congo brazzaville", "242"); // Republic of the Congo
        codes.put("Congo", "243");
        codes.put("Cook islands", "682");
        codes.put("Costa rica", "506");
        codes.put("Cote d'ivoire", "225");
        codes.put("Croatia", "385");
        codes.put("Cuba", "53");
        codes.put("Cyprus", "357");
        codes.put("Czech republic", "420");
        codes.put("Denmark", "45");
        codes.put("Djibouti", "253");
        codes.put("Dominica", "1-767");
        codes.put("Dominican republic", "1-809");
        codes.put("Ecuador", "593");
        codes.put("Egypt", "20");
        codes.put("El salvador", "503");
        codes.put("Equatorial guinea", "240");
        codes.put("Eritrea", "291");
        codes.put("Estonia", "372");
        codes.put("Ethiopia", "251");
        codes.put("Falkland islands", "500");
        codes.put("Faroe islands", "298");
        codes.put("Fiji", "679");
        codes.put("Finland", "358");
        codes.put("France", "33");
        codes.put("French guiana", "594");
        codes.put("French polynesia", "689");
        codes.put("French southern territories", "262");
        codes.put("Gabon", "241");
        codes.put("Gambia", "220");
        codes.put("Georgia", "995");
        codes.put("Germany", "49");
        codes.put("Ghana", "233");
        codes.put("Gibraltar", "350");
        codes.put("Greece", "30");
        codes.put("Greenland", "299");
        codes.put("Grenada", "1-473");
        codes.put("Guadeloupe", "590");
        codes.put("Guam", "1-671");
        codes.put("Guatemala", "502");
        codes.put("Guernsey", "44");
        codes.put("Guinea", "224");
        codes.put("Guinea bissau", "245");
        codes.put("Guyana", "592");
        codes.put("Haiti", "509");
        codes.put("Heard island and mcdonald islands", "672");
        codes.put("Holy see", "379");
        codes.put("Honduras", "504");
        codes.put("Hong kong", "852");
        codes.put("Hungary", "36");
        codes.put("Iceland", "354");
        codes.put("India", "91");
        codes.put("Indonesia", "62");
        codes.put("Iran", "98");
        codes.put("Iraq", "964");
        codes.put("Ireland", "353");
        codes.put("Isle of man", "44");
        codes.put("Israel", "972");
        codes.put("Italy", "39");
        codes.put("Jamaica", "1-876");
        codes.put("Japan", "81");
        codes.put("Jersey", "44");
        codes.put("Jordan", "962");
        codes.put("Kazakhstan", "7");
        codes.put("Kenya", "254");
        codes.put("Kiribati", "686");
        codes.put("South Korea", "850");
        codes.put("North korea", "82");
        codes.put("Kuwait", "965");
        codes.put("Kyrgyzstan", "996");
        codes.put("Lao people's democratic republic", "856");
        codes.put("Latvia", "371");
        codes.put("Lebanon", "961");
        codes.put("Lesotho", "266");
        codes.put("Liberia", "231");
        codes.put("Libyan arab jamahiriya", "218");
        codes.put("Liechtenstein", "423");
        codes.put("Lithuania", "370");
        codes.put("Luxembourg", "352");
        codes.put("Macao", "853");
        codes.put("Macedonia", "389");
        codes.put("Madagascar", "261");
        codes.put("Malawi", "265");
        codes.put("Malaysia", "60");
        codes.put("Maldives", "960");
        codes.put("Mali", "223");
        codes.put("Malta", "356");
        codes.put("Marshall Islands", "692");
        codes.put("Martinique", "596");
        codes.put("Mauritania", "222");
        codes.put("Mauritius", "230");
        codes.put("Mayotte", "262");
        codes.put("Mexico", "52");
        codes.put("Micronesia", "691");
        codes.put("Moldova", "373");
        codes.put("Monaco", "377");
        codes.put("Mongolia", "976");
        codes.put("Montserrat", "1-664");
        codes.put("Morocco", "212");
        codes.put("Mozambique", "258");
        codes.put("Myanmar", "95");
        codes.put("Namibia", "264");
        codes.put("Nauru", "674");
        codes.put("Nepal", "977");
        codes.put("Netherlands", "31");
        codes.put("Netherlands antilles", "599");
        codes.put("New caledonia", "687");
        codes.put("New zealand", "64");
        codes.put("Nicaragua", "505");
        codes.put("Niger", "227");
        codes.put("Nigeria", "234");
        codes.put("Niue", "683");
        codes.put("Norfolk island", "672");
        codes.put("Northern mariana islands", "1");
        codes.put("Norway", "47");
        codes.put("Oman", "968");
        codes.put("Pakistan", "92");
        codes.put("Palau", "680");
        codes.put("Palestine", "970");
        codes.put("Panama", "507");
        codes.put("Papua new guinea", "675");
        codes.put("Paraguay", "595");
        codes.put("Peru", "51");
        codes.put("Philippines", "63");
        codes.put("Pitcairn", "870");
        codes.put("Poland", "48");
        codes.put("Portugal", "351");
        codes.put("Puerto rico", "1");
        codes.put("Qatar", "974");
        codes.put("Reunion", "262");
        codes.put("Romania", "40");
        codes.put("Russia", "7");
        codes.put("Rwanda", "250");
        codes.put("Saint helena", "290");
        codes.put("Saint kitts and nevis", "1");
        codes.put("Saint lucia", "1");
        codes.put("Saint pierre", "508");
        codes.put("Saint vincent and the grenadines", "1");
        codes.put("Samoa", "685");
        codes.put("San marino", "378");
        codes.put("Sao tome and principe", "239");
        codes.put("Saudi arabia", "966");
        codes.put("Senegal", "221");
        codes.put("Serbia and montenegro", "381");
        codes.put("Seychelles", "248");
        codes.put("Sierra leone", "232");
        codes.put("Singapore", "65");
        codes.put("Slovakia", "421");
        codes.put("Slovenia", "386");
        codes.put("Solomon islands", "677");
        codes.put("Somalia", "252");
        codes.put("South africa", "27");
        codes.put("South georgia", "500");
        codes.put("Spain", "34");
        codes.put("Sri lanka", "94");
        codes.put("Sudan", "249");
        codes.put("Suriname", "597");
        codes.put("Svalbard and jan mayen", "47");
        codes.put("Swaziland", "268");
        codes.put("Sweden", "46");
        codes.put("Switzerland", "41");
        codes.put("Syria", "963");
        codes.put("Taiwan", "886");
        codes.put("Tajikistan", "992");
        codes.put("Tanzania", "255");
        codes.put("Thailand", "66");
        codes.put("Timor-leste", "670");
        codes.put("Togo", "228");
        codes.put("Tokelau", "690");
        codes.put("Tonga", "676");
        codes.put("Trinidad and Tobago", "1");
        codes.put("Tunisia", "216");
        codes.put("Turkey", "90");
        codes.put("Turkmenistan", "993");
        codes.put("Turks and caicos islands", "1");
        codes.put("Tuvalu", "688");
        codes.put("Uganda", "256");
        codes.put("Ukraine", "380");
        codes.put("United arab emirates", "971");
        codes.put("United kingdom", "44");
        codes.put("United states", "1");
        codes.put("United states minor outlying islands", "1");
        codes.put("Uruguay", "598");
        codes.put("Uzbekistan", "998");
        codes.put("Vanuatu", "678");
        codes.put("Venezuela", "58");
        codes.put("Vietnam", "84");
        codes.put("South sudan", "211");
        codes.put("Virgin islands", "1");
        codes.put("Virgin islands, U.S.", "1");
        codes.put("Wallis and futuna", "681");
        codes.put("Western sahara", "212");
        codes.put("Yemen", "967");
        codes.put("Zambia", "260");
        codes.put("Zimbabwe", "263");


        COUNTRY_CODES = Collections.unmodifiableMap(codes);
    }

    public static String getCountryCode(String countryName) {
        String toLower = countryName.toLowerCase();
        String code = COUNTRY_CODES.get(toLower.substring(0, 1).toUpperCase() + toLower.substring(1).toLowerCase());
        if (code == null) {
            //throw new IllegalArgumentException("This is un Unknown country: " + countryName);
             code = "000";



        }
        return code;
    }

    public static Map<String, String> getAllCountryCodes() {
        return new HashMap<>(COUNTRY_CODES); // Return a copy to prevent modifications
    }

}
