export interface IExecutionHistory {
  id?: number;
  jobName?: string;
  jobGroup?: string;
  triggerName?: string;
  triggerGroup?: string;
  jobClass?: string;
  firedTime?: Date;
  finishedTime?: Date;
  duration?: string;
  jobStatus?: string;
  jobMapData?: Object;
}
export class ExecutionHistory implements IExecutionHistory {
  constructor(
    public id?: number,
    public jobName?: string,
    public jobGroup?: string,
    public description?: string,
    public jobClass?: string,
    public duration?: string,
    public firedTime?: Date,
    public finishedTime?: Date,
    public jobMapData?: Object,
    public jobStatus?: string
  ) {}
}
