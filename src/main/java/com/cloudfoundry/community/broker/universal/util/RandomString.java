package com.cloudfoundry.community.broker.universal.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomString
{

  private static final Random RANDOM = new SecureRandom();

  public static String generateRandomString(int length)
  {
      String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

      String random = "";
      for (int i=0; i<length; i++)
      {
          int index = (int)(RANDOM.nextDouble()*letters.length());
          random += letters.substring(index, index+1);
      }
      return random;
  }
}
