package jetbrains.jetpad.model.collections.wrappers;

import jetbrains.jetpad.base.function.Function;
import jetbrains.jetpad.model.collections.CollectionAdapter;
import jetbrains.jetpad.model.collections.CollectionItemEvent;
import jetbrains.jetpad.model.collections.CollectionListener;
import jetbrains.jetpad.model.collections.list.ObservableArrayList;
import jetbrains.jetpad.model.collections.list.ObservableList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jetbrains.jetpad.model.collections.ObservableItemEventMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class ObservableListWrapperTest {
  private ObservableList<Double> source = new ObservableArrayList<>();
  private Function<Double, Integer> toTarget = new Function<Double, Integer>() {
    @Override
    public Integer apply(Double value) {
      return value.intValue() + 1;
    }
  };
  private Function<Integer, Double> toSource = new Function<Integer, Double>() {
    @Override
    public Double apply(Integer value) {
      return Integer.valueOf(value - 1).doubleValue();
    }
  };
  private ObservableList<Integer> target = new ObservableListWrapper<>(source, toTarget, toSource);

  private List<CollectionItemEvent<?extends Integer>> addEvents = new ArrayList<>();
  private List<CollectionItemEvent<?extends Integer>> setEvents = new ArrayList<>();
  private List<CollectionItemEvent<?extends Integer>> removeEvents = new ArrayList<>();
  private CollectionListener<Integer> listener = new CollectionListener<Integer>() {
    @Override
    public void onItemAdded(CollectionItemEvent<? extends Integer> event) {
      addEvents.add(event);
    }

    @Override
    public void onItemSet(CollectionItemEvent<? extends Integer> event) {
      setEvents.add(event);
    }

    @Override
    public void onItemRemoved(CollectionItemEvent<? extends Integer> event) {
      removeEvents.add(event);
    }
  };
  private void assertEvents(int addCount, int setCount, int removeCount) {
    assertThat(addEvents,    hasSize(addCount));
    assertThat(setEvents,    hasSize(setCount));
    assertThat(removeEvents, hasSize(removeCount));
  }


  @Before
  public void setup() {
    source.addAll(Arrays.asList(10.0, 20.0, 30.0));
    target.addListener(listener);
  }

  @Test
  public void listMapMaps() {
    assertThat(target, contains(11, 21, 31));
  }

  @Test
  public void listMapAddSource() {
    source.add(1, 15.0);
    assertThat(target, contains(11, 16, 21, 31));
    assertEvents(1, 0, 0);
    assertThat(addEvents.get(0), is(addEvent(equalTo(16), equalTo(1))));
  }

  @Test
  public void listMapAddTarget() {
    target.add(1, 15);
    assertThat(target, contains(11, 15, 21, 31));
    assertThat(source, contains(10.0, 14.0, 20.0, 30.0));
    assertEvents(1, 0, 0);
    assertThat(addEvents.get(0), is(addEvent(equalTo(15), equalTo(1))));
  }

  @Test
  public void listMapSetSource() {
    source.set(0, 15.0);
    assertThat(target, contains(16, 21, 31));
    assertEvents(0, 1, 0);
    assertThat(setEvents.get(0), is(setEvent(equalTo(11), equalTo(16), equalTo(0))));
  }

  @Test
  public void listMapSetTarget() {
    target.set(0, 15);
    assertThat(target, contains(15, 21, 31));
    assertThat(source, contains(14.0, 20.0, 30.0));
    assertEvents(0, 1, 0);
    assertThat(setEvents.get(0), is(setEvent(equalTo(11), equalTo(15), equalTo(0))));
  }

  @Test
  public void listMapRemoveSource() {
    source.remove(2);
    assertThat(target, contains(11, 21));
    assertEvents(0, 0, 1);
    assertThat(removeEvents.get(0), is(removeEvent(equalTo(31), equalTo(2))));
  }

  @Test
  public void listMapRemoveTarget() {
    target.remove(2);
    assertThat(target, contains(11, 21));
    assertThat(source, contains(10.0, 20.0));
    assertEvents(0, 0, 1);
    assertThat(removeEvents.get(0), is(removeEvent(equalTo(31), equalTo(2))));
  }

  @Test
  public void listMapListenerOrderOnSourceAdd1() {
    final boolean[] sourceFired = {false};
    final boolean[] targetFired = {false};

    source.addListener(new CollectionAdapter<Double>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Double> event) {
        assertThat(sourceFired[0], is(false));
        assertThat(targetFired[0], is(false));
        sourceFired[0] = true;
      }
    });
    target.addListener(new CollectionAdapter<Integer>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Integer> event) {
        assertThat(sourceFired[0], is(true));
        assertThat(targetFired[0], is(false));
        targetFired[0] = true;
      }
    });
    source.add(0, 0.0);
    assertThat(sourceFired[0], is(true));
    assertThat(targetFired[0], is(true));
  }

  @Test
  public void listMapListenerOrderOnSourceAdd2() {
    final boolean[] sourceFired = {false};
    final boolean[] targetFired = {false};

    target.addListener(new CollectionAdapter<Integer>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Integer> event) {
        assertThat(sourceFired[0], is(false));
        assertThat(targetFired[0], is(false));
        targetFired[0] = true;
      }
    });
    source.addListener(new CollectionAdapter<Double>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Double> event) {
        assertThat(sourceFired[0], is(false));
        assertThat(targetFired[0], is(true));
        sourceFired[0] = true;
      }
    });
    source.add(0, 0.0);
    assertThat(sourceFired[0], is(true));
    assertThat(targetFired[0], is(true));
  }

  @Test
  public void listMapListenerOrderOnTargetAdd1() {
    final boolean[] sourceFired = {false};
    final boolean[] targetFired = {false};

    source.addListener(new CollectionAdapter<Double>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Double> event) {
        assertThat(sourceFired[0], is(false));
        assertThat(targetFired[0], is(false));
        sourceFired[0] = true;
      }
    });
    target.addListener(new CollectionAdapter<Integer>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Integer> event) {
        assertThat(sourceFired[0], is(true));
        assertThat(targetFired[0], is(false));
        targetFired[0] = true;
      }
    });
    target.add(0, 0);
    assertThat(sourceFired[0], is(true));
    assertThat(targetFired[0], is(true));
  }

  @Test
  public void listMapListenerOrderOnTargetAdd2() {
    final boolean[] sourceFired = {false};
    final boolean[] targetFired = {false};

    target.addListener(new CollectionAdapter<Integer>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Integer> event) {
        assertThat(sourceFired[0], is(false));
        assertThat(targetFired[0], is(false));
        targetFired[0] = true;
      }
    });
    source.addListener(new CollectionAdapter<Double>() {
      @Override
      public void onItemAdded(CollectionItemEvent<? extends Double> event) {
        assertThat(sourceFired[0], is(false));
        assertThat(targetFired[0], is(true));
        sourceFired[0] = true;
      }
    });
    target.add(0, 0);
    assertThat(sourceFired[0], is(true));
    assertThat(targetFired[0], is(true));
  }
}
