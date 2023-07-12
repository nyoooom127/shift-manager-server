package assignsShifts.models.enums;

public enum ShiftOptionEnum {
  OJT() {
    @Override
    public double getScoreToAdd() {
      return 1;
    }
  },
  LEV {
    @Override
    public double getScoreToAdd() {
      return 1;
    }
  },
  PRIMARY {
    @Override
    public double getScoreToAdd() {
      return 1;
    }
  },
  SECONDARY {
    @Override
    public double getScoreToAdd() {
      return 0;
    }
  },
  INTEGRATION {
    @Override
    public double getScoreToAdd() {
      return 1;
    }
  };

  public abstract double getScoreToAdd();
}
