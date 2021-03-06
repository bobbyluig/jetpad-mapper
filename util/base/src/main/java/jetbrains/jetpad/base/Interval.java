/*
 * Copyright 2012-2016 JetBrains s.r.o
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.jetpad.base;

public final class Interval {
  private final int myLowerBound;
  private final int myUpperBound;

  public Interval(int lowerBound, int upperBound) {
    if (lowerBound > upperBound) {
      throw new IllegalArgumentException("Lower bound is greater than upper: lower bound=" + lowerBound
          + ", upper bound=" + upperBound);
    }
    myLowerBound = lowerBound;
    myUpperBound = upperBound;
  }

  public int getLowerBound() {
    return myLowerBound;
  }

  public int getUpperBound() {
    return myUpperBound;
  }

  public int getLength() {
    return myUpperBound - myLowerBound;
  }

  public boolean contains(int point) {
    return myLowerBound <= point && point <= myUpperBound;
  }

  public boolean contains(Interval other) {
    return contains(other.getLowerBound()) && contains(other.getUpperBound());
  }

  public boolean intersects(Interval other) {
    return contains(other.getLowerBound()) ||  other.contains(getLowerBound());
  }

  /**
   * Returns minimal interval that contains both this and other intervals.
   */
  public Interval union(Interval other) {
    return new Interval(Math.min(myLowerBound, other.myLowerBound), Math.max(myUpperBound, other.myUpperBound));
  }

  public Interval add(int delta) {
    return new Interval(myLowerBound + delta, myUpperBound + delta);
  }

  public Interval sub(int delta) {
    return new Interval(myLowerBound - delta, myUpperBound - delta);
  }

  @Override
  public String toString() {
    return "[" + myLowerBound + ", " + myUpperBound + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Interval)) return false;

    Interval interval = (Interval) o;
    if (myLowerBound != interval.myLowerBound) return false;
    return myUpperBound == interval.myUpperBound;
  }

  @Override
  public int hashCode() {
    int result = myLowerBound;
    result = 31 * result + myUpperBound;
    return result;
  }
}