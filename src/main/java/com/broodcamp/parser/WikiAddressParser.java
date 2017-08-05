package com.broodcamp.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiAddressParser {

	private static final String WIKI_BASE_URL = "https://en.wikipedia.org";

	public static void main(String[] args) {
		try {
			new WikiAddressParser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WikiAddressParser() throws IOException {
		Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/Provinces_of_the_Philippines").get();
		Elements provinces = doc.select("table.wikitable.sortable.plainrowheaders")
				.select("td[style=text-align:left;font-weight:bold;]").select("a");
		for (Element el : provinces) {
			if (!el.toString().contains("cite_note")) {
				String provinceName = el.text();
				String provinceHref = el.attr("href");
				// System.out.println(provinceName + " | " + provinceHref);
				if (!provinceName.equals("Pampanga")) {
//					continue;
				}

				String citiesUrl = WIKI_BASE_URL + provinceHref;
				// System.out.println("Cities.url=" + citiesUrl);
				Document doc2 = Jsoup.connect(citiesUrl).get();
				Element townTable = doc2.select("table.wikitable.sortable").first();

				if (townTable == null) {
					System.out.println("ERROR: Missing province name=" + provinceName);
					continue;
				}

				Elements rows = townTable.select("tbody tr");

				boolean isCity = false;
				for (Element row : rows) {
					if (row.attr("style").equals("background-color:#FFE6F3;")) {
						isCity = true;
					}

					if (row.attr("class").equals("sortbottom")) {
						continue;
					}
					Elements townTds = row.select("td[style*=text-align:left;font-weight:bold;]");
					Element townTd = townTds.first();
					if (townTds.size() == 0) {
						townTd = row.select("th[style*=text-align:left;]").first();
						if (townTd == null) {
							townTd = row.select("th[style=background-color:initial;]").first();
							if (townTd == null) {
								townTd = row.select("td[style=text-align:left;border-style:solid hidden solid solid;]")
										.first();
								if (townTd == null) {
									townTd = row
											.select("td[style=text-align:left; background:#fff895; border-right:0;]")
											.first();
								}
							}
						}
					}

					if (townTd == null) {
						continue;
					}
					try {
						Element townsA = townTd.select("a").first();
						if (townsA == null) {
							continue;
						}
						String latitude = row.select("span.latitude").text();
						String longitude = row.select("span.longitude").text();
						String townName = townsA.text();

						if (!townName.equals("[i]")) {
							System.out.println(
									provinceName + "," + townName + "," + isCity + "," + latitude + "," + longitude);
						}
					} catch (NullPointerException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}

}
