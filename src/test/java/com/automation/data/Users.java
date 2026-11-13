package com.automation.data;

import com.automation.config.TestConfig;

/** Known Swag Labs accounts. The password comes from configuration, never from code. */
public final class Users {

    public static final User STANDARD = new User("standard_user", TestConfig.saucePassword());
    public static final User LOCKED_OUT = new User("locked_out_user", TestConfig.saucePassword());

    private Users() {
    }
}
