import { ExecutionHistory } from '../execution-history/executionHistory';

export interface ITrigger {
  id?: number;
  jobName?: string;
  jobGroup?: string;
  triggerName?: string;
  triggerGroup?: string;
  triggerType?: string;
  triggerState?: string;
  description?: string;
  lastExecutionTime?: Date;
  nextExecutionTime?: Date;
  startTime?: Date;
  endTime?: Date;
  cronExpression?: string;
  repeatInterval?: string;
  repeatIndefinitely?: boolean;
  repeatCount?: number;
  jobMapData?: Map<string, string>;
  executionHistory?: Array<ExecutionHistory>;
}
