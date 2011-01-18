package com.google.inject.stat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class Stats {
  private final Map<String, StatDescriptor> stats = new MapMaker().makeMap();

  void register(StatDescriptor statDescriptor) {
    StatDescriptor first = stats.get(statDescriptor.getName());
    if (null != first) {
      throw new IllegalArgumentException(String.format(
          "You have two stats using the same name [%s] in different types, this is not allowed. \n"
          + "First encounter: %s\nSecond encounter: %s", statDescriptor.getName(), first.getName(),
          statDescriptor.getMember().getDeclaringClass()));
    }

    stats.put(statDescriptor.getName(), statDescriptor);
  }

  ImmutableMap<StatDescriptor, Object> snapshot() {
    ImmutableMap.Builder<StatDescriptor, Object> builder =
        ImmutableMap.builder();
    for (StatDescriptor statDescriptor : stats.values()) {
      builder.put(statDescriptor, read(statDescriptor));
    }
    return builder.build();
  }

  Object read(StatDescriptor statDescriptor) {
    Object target = statDescriptor.getTarget();

    Member member = statDescriptor.getMember();
    if (member instanceof Field) {
      Field field = (Field) member;
      if (!field.isAccessible()) {
        field.setAccessible(true);
      }
      try {
        return field.get(target);
      } catch (IllegalAccessException e) {
        return "unable to read: " + e.getMessage();
      }
    }

    if (member instanceof Method) {
      Method method = (Method) member;
      if (!method.isAccessible()) {
        method.setAccessible(true);
      }
      try {
        return method.invoke(target);
      } catch (InvocationTargetException e) {
        return "unable to read: " + e.getMessage();
      } catch (IllegalAccessException e) {
        return "unable to read: " + e.getMessage();
      }
    }

    throw new IllegalArgumentException(
        "Unexpected member type on descriptor: " + statDescriptor);
  }
}