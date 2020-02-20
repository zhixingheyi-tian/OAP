package org.apache.orc.impl;

import org.apache.orc.storage.common.io.DiskRangeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnDiskRangeList extends DiskRangeList {

  private static final Logger LOG =
          LoggerFactory.getLogger(ColumnDiskRangeList.class);


  final int columnId;
  final int currentStripe;

  public ColumnDiskRangeList(int columnId, int currentStripe, long offset, long end) {
    super(offset, end);
    this.columnId = columnId;
    this.currentStripe = currentStripe;
  }

  public static class CreateColumnRangeHelper {
    private DiskRangeList tail = null;
    private DiskRangeList head;

    public CreateColumnRangeHelper() {
    }

    public DiskRangeList getTail() {
      return this.tail;
    }

    public void add(int columnId, int currentStripe, long offset, long end) {
      DiskRangeList node = new ColumnDiskRangeList(columnId, currentStripe, offset, end);
      if (this.tail == null) {
        this.head = this.tail = node;
      } else {
        this.tail = this.tail.insertAfter(node);
      }
    }

    public DiskRangeList get() {
      return this.head;
    }

    public DiskRangeList extract() {
      DiskRangeList result = this.head;
      this.head = null;
      return result;
    }
  }
}
