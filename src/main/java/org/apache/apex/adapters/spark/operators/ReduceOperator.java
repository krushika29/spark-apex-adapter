package org.apache.apex.adapters.spark.operators;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;

import scala.Function2;

public class ReduceOperator extends BaseOperator
{
  public Function2 f;
  Object previousValue = null;
  Object finalValue = null;
  private boolean done = false;

  @Override
  public void beginWindow(long windowId)
  {
    if (done) {
      System.out.println("Emitted Final Value: " + finalValue);
      output.emit(finalValue);
      done = false;
    }
  }

  public final transient DefaultInputPort<Object> input = new DefaultInputPort<Object>()
  {
    @Override
    public void process(Object tuple)
    {
      if (previousValue == null) {
        previousValue = tuple;
        finalValue = tuple;
      } else {
        finalValue = f.apply(previousValue, tuple);
        System.out.println("Current Final Value: " + finalValue);
      }
    }
  };

  public final transient DefaultInputPort<Boolean> controlDone = new DefaultInputPort<Boolean>()
  {
    @Override
    public void process(Boolean tuple)
    {
      done = true;
    }
  };
  public final transient DefaultOutputPort<Object> output = new DefaultOutputPort<>();
}
