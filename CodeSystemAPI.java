/*
 * Copyright (c) Gilljan 2020-2022. All rights reserved.
 */

package de.gilljan.codesystem.api;

import de.gilljan.codesystem.mysql.MySQLCodes;
import de.gilljan.codesystem.utils.GenerateCodeUtil;

import java.sql.SQLException;

import static org.bukkit.Bukkit.getServer;

public class CodeSystemAPI {

    /**
     * @param allowedUsages Sets the allowed usages for the code. Unlimited usages: -1
     * @param code The code
     * @param economy Sets the money which will be added. Null if another type of code
     * @param type The type of the Code
     * @param value Values for Command, permission or group. Null, if economy type is used
     * @return successfully created
     */
    public boolean createCode(Type type, String code, String value, Integer economy, long allowedUsages) {
        try {
            if((value == null || economy == null) && type != null && code != null) {
                if(!MySQLCodes.checkCode(code)) {
                    switch (type) {
                        case ECONOMY:
                            if(getServer().getPluginManager().isPluginEnabled("Vault")) {
                                MySQLCodes.setCode(code, type.getType(), allowedUsages, 0, String.valueOf(economy));
                                return true;
                            }
                            break;
                        case COMMAND:
                            String[] command = value.split(" ");
                            if(command.length >= 1) {
                                MySQLCodes.setCode(code, type.getType(), allowedUsages, 0, value);
                                return true;
                            }
                            break;
                        case GROUP:
                        case PERMISSION:
                            String[] group = value.split(" ");
                            if(group.length == 1) {
                                MySQLCodes.setCode(code, type.getType(), allowedUsages, 0, value);
                                return true;
                            }
                            break;
                    }

                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param allowedUsages Sets the allowed usages for the code. Unlimited usages: -1
     * @param economy Sets the money which will be added. Null if another type of code
     * @param type The type of the Code
     * @param value Values for Command, permission or group. Null, if economy type is used
     * @return the generated code according to the pattern
     */
    public String generateCode(Type type, String value, Integer economy, long allowedUsages) {
        try {
            if((value == null || economy == null) && type != null) {
                String code = GenerateCodeUtil.generateCode();
                if(!MySQLCodes.checkCode(code)) {
                    switch (type) {
                        case ECONOMY:
                            if(getServer().getPluginManager().isPluginEnabled("Vault")) {
                                MySQLCodes.setCode(code, type.getType(), allowedUsages, 0, String.valueOf(economy));
                            }
                            break;
                        case COMMAND:
                            String[] command = value.split(" ");
                            if(command.length >= 1) {
                                MySQLCodes.setCode(code, type.getType(), allowedUsages, 0, value);
                            }
                            break;
                        case GROUP:
                        case PERMISSION:
                            String[] group = value.split(" ");
                            if(group.length == 1) {
                                MySQLCodes.setCode(code, type.getType(), allowedUsages, 0, value);
                            }
                            break;
                    }
                    return code;
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param code The code which will be checked
     * @return true, if the code exists
     */
    public boolean checkCode(String code) {
        try {
            if(code != null) {
                return MySQLCodes.checkCode(code);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param code The code which will be checked
     * @return the used times of the code. Returns 0, if the code does not exists
     */
    public long getUsages(String code) {
        if(code != null) {
            return MySQLCodes.getUsedTimes(code);
        }
        return 0;
    }


    /**
     *
     * @param code The code which will be checked
     * @return the allowed usages of the code. Returns 0, if the code does not exists
     */
    public long getAllowedUsages(String code) {
        if(code != null) {
            return MySQLCodes.getDuration(code);
        }
        return 0;
    }

    /**
     *
     * @param code The code which will be checked
     * @return the remaining times. If unlimited: -1. Returns 0, if the code does not exists
     */
    public long getRemainingUsages(String code) {
        if(code != null) {
            if(MySQLCodes.getDuration(code) != -1) {
                return MySQLCodes.getDuration(code) - MySQLCodes.getUsedTimes(code);
            } else return -1;
        }
        return 0;
    }

    /**
     *
     * @param code The code which will be checked
     * @return the set value. Type economy: also a string.
     */
    public String getValue(String code) {
        if(code != null) {
            return MySQLCodes.getValue(code);
        }
        return null;
    }

    /**
     *
     * @param code The code which will be checked
     * @return the type of the code.
     */
    public Type getType(String code) {
        if(code != null) {
            switch (MySQLCodes.getType(code)) {
                case "cmd":
                    return Type.COMMAND;
                case "perm":
                    return Type.PERMISSION;
                case "eco":
                    return Type.ECONOMY;
                case "group":
                    return Type.GROUP;
            }
        }
        return null;
    }

    /**
     *
     * @param code Deletes the given code, if it exists.
     */
    public void deleteCode(String code) {
        if(code != null) {
            MySQLCodes.deleteCode(code);
        }
    }



    public enum Type {
        COMMAND("cmd"),
        PERMISSION("perm"),
        GROUP("group"),
        ECONOMY("eco");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

}
