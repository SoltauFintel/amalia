package github.soltaufintel.amalia.web.form;

import org.junit.Test;

import github.soltaufintel.amalia.web.templating.ColumnFormularGenerator;
import github.soltaufintel.amalia.web.templating.FormularGenerator;

public class FormularGeneratorTest {

	@Test
	public void test() {
		System.out.println(new FormularGenerator(2)
			.withVersion()
			.newline()
				.textfield("eingabe", "Eingabe", 3, true, true)
				.textfield("mitgliedSeit", "Mitglied seit", 1)
			.newline()
				.textfield("enter", "Enterhaken", 3)
			.newline()
				.combobox("abteilung", "Abteilung", 2, "abteilungen")
			.getHTML("/save/{{id}}", "/"));
	}

	@Test
	public void testOneColumn() {
		System.out.println(new ColumnFormularGenerator(2, 1)
			.withVersion()
			.textfield("eingabe", "Eingabe", 3, true, true)
			.textfield("mitgliedSeit", "Mitglied seit", 1)
			.textfield("enter", "Enterhaken", 3)
			.combobox("abteilung", "Abteilung", 2, "abteilungen")
			.getHTML("/save/{{id}}", "/"));
	}

	@Test
	public void testTwoColumns() {
		System.out.println(new ColumnFormularGenerator(2, 2)
			.withVersion()
			.textfield("eingabe", "Eingabe", 3, true, true)
			.textfield("mitgliedSeit", "Mitglied seit", 1)
			.combobox("abteilung", "Abteilung", 2, "abteilungen")
			.getHTML("/save/{{id}}", "/"));
	}

	@Test
	public void testThreeColumns() {
		System.out.println(new ColumnFormularGenerator(2, 3)
			.withVersion()
			.textfield("eingabe", "Eingabe", 3, true, true)
			.textfield("mitgliedSeit", "Mitglied seit", 1)
			.textfield("enter", "Enterhaken", 3)
			.combobox("abteilung", "Abteilung", 2, "abteilungen")
			.getHTML("/save/{{id}}", "/"));
	}
}
