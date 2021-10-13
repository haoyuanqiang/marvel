import type { Position, PositionsModelState } from './positions';
import type { PositionEditorModelState } from './positionEditor';

export { Position, PositionModelState };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    positions?: boolean;
    positionEditor: boolean;
  };
}

export interface ConnectState {
  loading: Loading;
  positions: PositionsModelState;
  positionEditor: PositionEditorModelState;
}
