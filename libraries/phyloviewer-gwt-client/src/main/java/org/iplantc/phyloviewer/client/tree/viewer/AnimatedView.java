package org.iplantc.phyloviewer.client.tree.viewer;

import org.iplantc.phyloviewer.client.events.RenderEvent;
import org.iplantc.phyloviewer.shared.math.Box2D;
import org.iplantc.phyloviewer.shared.render.AnimateCamera;
import org.iplantc.phyloviewer.shared.render.Camera;

import com.google.gwt.user.client.Timer;

/**
 * A View that animates some of the camera moves
 */
public abstract class AnimatedView extends View
{
	private int numberOfAnimationSteps = 6;
	private AnimateCamera animator;
	private Timer renderTimer = new Timer()
	{
		public void run()
		{
			if(AnimatedView.this.isReady())
			{
				renderFrame();
			}
			else
			{
				this.schedule(33);
			}
		}
	};
	
	public AnimatedView()
	{
	}

	private void renderFrame()
	{
		if(animator != null)
		{
			getCamera().setViewMatrix(animator.getNextMatrix());

			if(animator.isDone())
			{
				// We are done.  clear the animator and cancel the timer.
				animator = null;
				renderTimer.cancel();
			}
		}

		// Dispatch a render event. This will make sure all views are updated with this camera.
		this.dispatch(new RenderEvent());
	}

	/**
	 * Starts animating this AnimatedView's camera from its current view matrix to the given camera's
	 * view matrix.
	 */
	protected void startAnimation(Camera finalCamera)
	{
		animator = new AnimateCamera(getCamera().getViewMatrix(), finalCamera.getViewMatrix(),
				numberOfAnimationSteps);
		renderTimer.scheduleRepeating(30);
	}

	@Override
	public void zoomToBoundingBox(Box2D boundingBox)
	{
		Camera finalCamera = getCamera().create();
		finalCamera.zoomToBoundingBox(boundingBox);

		startAnimation(finalCamera);
	}

	public int getNumberOfAnimationSteps()
	{
		return numberOfAnimationSteps;
	}

	/**
	 * Set the number of steps until the animation reaches the final state.
	 */
	public void setNumberOfAnimationSteps(int numberOfAnimationSteps)
	{
		this.numberOfAnimationSteps = numberOfAnimationSteps;
	}
}
