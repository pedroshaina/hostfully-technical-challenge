package com.hostfully.technicalchallenge.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.EasyRandomParameters.Range;

public class RandomEntityGenerator {
  private final static EasyRandom easyRandom = new EasyRandom(createEasyRandomParameters());

  public static <V> V create(final Class<V> classType) {
    return easyRandom.nextObject(classType);
  }

  public static <V> List<V> createList(final Class<V> classType, final int size) {
    final List<V> result = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      result.add(create(classType));
    }
    return result;
  }

  private static EasyRandomParameters createEasyRandomParameters() {
    final EasyRandomParameters params = new EasyRandomParameters();
    params.setSeed(new Random().nextLong());
    params.setCollectionSizeRange(new Range<>(1, 5));
    params.setCharset(StandardCharsets.UTF_8);
    params.setStringLengthRange(new Range<>(10, 255));
    return params;
  }
}
