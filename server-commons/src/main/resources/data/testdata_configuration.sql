INSERT INTO configuration(configKey, configValue, dataType)
	VALUES ('um_hashIterations', '200000', 'Integer'),
		('um_pwValidator_minLength', '6', 'Integer'),
		('um_pwValidator_maxLength', '20', 'Integer'),
		('um_pwValidator_numberCount', '1', 'Integer'),
		('um_pwValidator_specialCharsCount', '1', 'Integer'),
		('um_pwValidator_lowerCaseCharsCount', '1', 'Integer'),
		('um_pwValidator_upperCaseCharCount', '1', 'Integer'),
		('um_pwValidator_allowedSpecialChars', '"][?\\/<~#`''!@$%^&§*()+-=}\"|:;,>{"', 'String'),
		('um_maxLoginRetries', '5', 'Integer'),
		('um_authTokenValidTime', '10800', 'String');
