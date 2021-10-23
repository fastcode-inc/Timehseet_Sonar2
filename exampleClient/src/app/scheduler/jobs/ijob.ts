import { ExecutionHistory } from '../execution-history/executionHistory';
import { ITrigger } from '../triggers/trigger';

export interface IJob {
  id?: number;
  jobName?: string;
  jobGroup?: string;
  jobDescription?: string;
  jobClass?: string;
  jobStatus?: string;
  isDurable?: boolean;
  jobMapData?: Map<string, string>;
  triggerDetails?: Array<ITrigger>;
  executionHistory?: Array<ExecutionHistory>;
}
