package com.atguigu.gmall.test;

import java.util.Base64;

/**
 * jwt的回顾
 */
public class Test {

    /**
     * 解析jwt令牌
     * @param args
     */
    public static void main(String[] args) {
        byte[] a = Base64.getDecoder().decode("QyVIbEFyRSP66-tJn2Q_qf1EOclTtL3Kiy1u0oXXXgoohTGt5cbUXHchpax8rR14svTW6qI6_nmE4DpqWS5z2HoFo6R-reQwmtnn8qehlXo-x1ACwRE53UU7Yx4zQQXOf1mW3KRZO5iMmMrURgqhKV4-EKa_hcKrx50iBCied4N7bph-ZxNhDdWASPxEfjp79SISlMG2jaoGhCpz_d2tsKPtjaQeUC4XVDbX-Csst9mNfxOFmLc6yRpBfeLlWvRoWd2huMJHUUw_5eFoULpeMurl9LnhDNrOSn0T_dr16pO5HJgyXTYLczX2-_34rVMItkwt8xttVD6_idhNVW8Jtg");
        System.out.println(new String(a));
    }
}
