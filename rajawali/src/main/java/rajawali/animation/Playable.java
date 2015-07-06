package rajawali.animation;

import rajawali.renderer.AFrameTask;

public abstract class Playable extends AFrameTask implements IPlayable {

	protected static enum State {
		// @formatter:off
		PLAYING
		, PAUSED
		, ENDED;
		// @formatter:on
	}

	private State mState;
	
	
	public Playable() {
		mState = State.PAUSED;
	}

	@Override
	public TYPE getFrameTaskType() {
		return AFrameTask.TYPE.ANIMATION;
	}

	public boolean isEnded() {
		return mState == State.ENDED;
	}

	public boolean isPaused() {
		return mState == State.PAUSED;
	}

	public boolean isPlaying() {
		return mState == State.PLAYING;
	}

	public void pause() {
		mState = State.PAUSED;
	}

	public void play() {
		mState = State.PLAYING;
	}

	public void reset() {
		mState = State.PAUSED;
	}

	protected void setState(State state) {
		mState = state;
	}

}
