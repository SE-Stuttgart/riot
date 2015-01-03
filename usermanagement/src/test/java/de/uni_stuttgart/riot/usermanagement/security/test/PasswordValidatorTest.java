package de.uni_stuttgart.riot.usermanagement.security.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.configuration.Configuration;
import de.uni_stuttgart.riot.usermanagement.security.PasswordValidator;
import de.uni_stuttgart.riot.usermanagement.security.exception.PasswordValidationException;

/**
 * Test for the class {@link PasswordValidator}.
 *
 * @author Niklas Schnabel
 */
public class PasswordValidatorTest {

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateValidStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertTrue(pv.validate("123wE!"));
    }

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateTooShortStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate("23wE!"));
    }

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateTooLongStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate("111111111111111111111111123wE!"));
    }

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateMissingSpecialCharStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate("1234wE"));
    }

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateMissingNumberStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate("wwwwE!"));
    }

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateMissingLowerCaseCharStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate("1234E!"));
    }

    /**
     * Test method for {@link de.uni_stuttgart.riot.usermanagement.security.PasswordValidator#validate(java.lang.String)}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    @Test
    public void testValidateMissingUperCaseCharStandardConstructor() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate("1234E!"));
    }

    @Test
    public void testValidateComplexPasswordValid1() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(10, 20, 3, 2, 2, 2, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertTrue(pv.validate("1Q24e!§dqewqwe1/T"));
        assertTrue(pv.validate("\\k]>Bs22*~zNKC6c"));
        assertTrue(pv.validate("[F[H%8G2qS`<ct(7^;"));
        assertTrue(pv.validate("5>e+/5n-WW5"));
        assertTrue(pv.validate("gu%'W/M!dWV4y2s3~FvR"));
        assertTrue(pv.validate("U}42',xU{/0f"));
    }

    @Test
    public void testValidateComplexPasswordValid2() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(3, 10, 0, 1, 1, 0, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertTrue(pv.validate("aaBBcc"));
        assertTrue(pv.validate("12aU"));
        assertTrue(pv.validate("eEe"));
        assertTrue(pv.validate("1&/DffD/&1"));
        assertTrue(pv.validate("aaaaaaaaaA"));
        assertTrue(pv.validate("123*'*'Gh"));
    }

    @Test
    public void testValidateComplexPasswordValid3() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(1, 10, 0, 0, 0, 1, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertTrue(pv.validate("*"));
        assertTrue(pv.validate("123asdf#"));
        assertTrue(pv.validate("df%"));
        assertTrue(pv.validate("1&/DffD/&1"));
        assertTrue(pv.validate("/////////"));
        assertTrue(pv.validate("123*'*'Gh"));
    }

    @Test
    public void testValidateComplexPasswordInvalid1() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(10, 20, 3, 2, 2, 2, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertFalse(pv.validate("1Q24e!§dqewqwe1/T3412")); // too long
        assertFalse(pv.validate("123aaBB*")); // too short
        assertFalse(pv.validate("[F[H%GG2qS`<ct(7^;")); // too less numbers
        assertFalse(pv.validate("5>E+/5n-WW5")); // too less lower case chars
        assertFalse(pv.validate("gu%'w/m!dwv4y2s3~fvR")); // too less upper case chars
        assertFalse(pv.validate("Ud42xUO/0f")); // too less special chars
    }

    @Test
    public void testValidateComplexPasswordInvalid2() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(3, 10, 0, 1, 1, 0, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertFalse(pv.validate("aaBBccDDeeF")); // too long
        assertFalse(pv.validate("aU")); // too short
        assertFalse(pv.validate("EEEEE")); // too less lower case chars
        assertFalse(pv.validate("fffff")); // too less upper case chars
    }

    @Test
    public void testValidateComplexPasswordInvalid3() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(1, 10, 0, 0, 0, 1, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertFalse(pv.validate("")); // too short
        assertFalse(pv.validate("§§§§§§§§§§§")); // too long
        assertFalse(pv.validate("dfdsaf")); // too less special chars
    }

    @Test
    public void testValidateNull() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator();
        assertFalse(pv.validate(null));
    }

    @Test
    public void testGetter() throws PasswordValidationException {
        PasswordValidator pv = new PasswordValidator(5, 20, 1, 2, 3, 4, Configuration.getPasswordValidationAllowedSpecialCharacters());
        assertEquals(5, pv.getMinLength());
        assertEquals(20, pv.getMaxLength());
        assertEquals(1, pv.getMinAmountNumbers());
        assertEquals(2, pv.getMinAmountLowerCaseLetters());
        assertEquals(3, pv.getMinAmountUpperCaseLetters());
        assertEquals(4, pv.getMinAmountSpecialCharacters());
        assertEquals(Configuration.getPasswordValidationAllowedSpecialCharacters(), pv.getAllowedSpecialCharacters());
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructNullSpecialChars() throws PasswordValidationException {
        new PasswordValidator(1, 2, 0, 0, 0, 0, null);
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructMinLengthBiggerMaxLength() throws PasswordValidationException {
        new PasswordValidator(2, 1, 0, 0, 0, 0, "");
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructRestrictionsBiggerMaxLength() throws PasswordValidationException {
        new PasswordValidator(1, 3, 1, 1, 1, 1, "");
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructNegativeMinLength() throws PasswordValidationException {
        new PasswordValidator(-1, 1, 0, 0, 0, 0, "");
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructNegativeMinNumbers() throws PasswordValidationException {
        new PasswordValidator(0, 1, -1, 0, 0, 0, "");
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructNegativeMinLowerCaseChars() throws PasswordValidationException {
        new PasswordValidator(0, 1, 0, -1, 0, 0, "");
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructNegativeMinUpperCaseChars() throws PasswordValidationException {
        new PasswordValidator(0, 1, 0, 0, -1, 0, "");
    }

    @Test(expected = PasswordValidationException.class)
    public void testConstructNegativeMinSpecialChars() throws PasswordValidationException {
        new PasswordValidator(0, 1, 0, 0, 0, -1, "");
    }
}
