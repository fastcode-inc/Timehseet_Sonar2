export interface IExecutingJob {
  id?: number;
  jobName?: string;
  jobGroup?: string;
  triggerName?: string;
  triggerGroup?: string;
  jobClass?: string;
  firedTime?: Date;
  nextExecutionTime?: Date;
  jobMapData?: Map<string, string>;
}
