import { TruckReadOnlyDto } from "./truck-readonly-dto";
import { LocationNodeReadonlyDto } from "./location-node-readonly-dto";
import { RouteReadOnlyDto } from "./route-read-only-dto";

export interface PlanReadonlyDto {
  id: number;
  timeslotLength: number;
  createdAt: string;
  routeDtoList: RouteReadOnlyDto[];
  locationNodeReadOnlyDtoList: LocationNodeReadonlyDto[];
  truckReadOnlyDtoList: TruckReadOnlyDto[];
  planStatus: string;
  algorithmSpec: string;
}
