export type Department = {
  id?: string;
  code: string;
  name: string;
  status: number;
  sortNumber?: number;
  parentId: string;
  modifyTime?: number;
  children?: Department[];
}
